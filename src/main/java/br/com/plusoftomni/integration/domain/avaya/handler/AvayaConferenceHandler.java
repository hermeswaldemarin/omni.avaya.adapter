package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.ConferenceEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.Call;
import javax.telephony.Provider;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.callcontrol.CallControlTerminalConnection;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaConferenceHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaConferenceHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.CONFERENCE)
    public void execute(ConferenceEvent event){
        try {

            if(avayaService.getConsultCall() != null){

                logger.info("CONFERENCE -> Conference after consult = ");
                logger.info("CONFERENCE -> PASSO = 1 ");

                if((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[1] != null){

                    try {

                        Call ligacaoConferencia = avayaService.getConsultCall();
                        ((CallControlCall) ligacaoConferencia).setConferenceEnable(true);
                        ((CallControlCall) ligacaoConferencia).setConferenceController(avayaService.getActiveTerminal().getTerminalConnections()[1]);
                        ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[1]).hold();

                        ((CallControlCall) avayaService.getActiveCall()).setConferenceEnable(true);
//                        ((CallControlCall) avayaService.getActiveCall()).setConferenceController(avayaService.getActiveTerminal().getTerminalConnections()[0]);
                        ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();

                        logger.info("PROVIDER >>> " + avayaService.getProvider().getState());
                        logger.info("STATE OF ACTIVE CALL >>> " + ((CallControlCall) avayaService.getActiveCall()).getState());
                        logger.info("CALL CONTROL TERMINAL STATE 1 >>> " +((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[0]).getCallControlState());
                        logger.info("CALL CONTROL TERMINAL STATE 2 >>> " +((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[1]).getCallControlState());
                        logger.info("TERMINAL CONNECTION 1 >>> " + avayaService.getActiveTerminal().getTerminalConnections()[0].getState());
                        logger.info("TERMINAL CONNECTION 2 >>> " + avayaService.getActiveTerminal().getTerminalConnections()[1].getState());

                        if ( avayaService.getProvider().getState() == Provider.IN_SERVICE ) {

                            if ( ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[0]).getCallControlState() == CallControlTerminalConnection.TALKING ) {

                                if ( ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[1]).getCallControlState() == CallControlTerminalConnection.HELD ) {

                                    if ( avayaService.getActiveTerminal().getTerminalConnections()[0].getState() == TerminalConnection.ACTIVE ) {

                                        if ( avayaService.getActiveTerminal().getTerminalConnections()[1].getState() == TerminalConnection.ACTIVE ) {

                                            if (((CallControlCall) avayaService.getActiveCall()).getState() == Call.IDLE ||
                                                    ((CallControlCall) avayaService.getActiveCall()).getState() == Call.ACTIVE ) {

                                                if ( ligacaoConferencia.getState() == Call.IDLE || ligacaoConferencia.getState() == Call.ACTIVE ) {

                                                    ((CallControlCall) avayaService.getActiveCall()).conference( ligacaoConferencia );

                                                }


                                            }

                                        }

                                    }

                                }

                            }

                        }

                    } catch (Exception e) {
                        logger.info("CONFERENCE -> ERRO = 1 ");
                        logger.error("CONFERENCE transfere - >", e);

                    }
                }

            } else {

                if (avayaService.getActiveCall() == null) {
                    logger.info("Needed to be in a active call");
                    return;
                }

                ((CallControlCall) avayaService.getActiveCall()).setConferenceEnable(true);

                logger.trace("Informing the connection that control the conference");

                ((CallControlCall) avayaService.getActiveCall()).setConferenceController(avayaService.getActiveTerminal().getTerminalConnections()[0]);

                logger.trace("Putting the active call in held");

                ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[0]).hold();

                logger.trace("Create a new connection that will be used in the conference");

                Call ligacaoConferencia = avayaService.getProvider().createCall();
                avayaService.setLigacaoConferencia( ligacaoConferencia );

                ligacaoConferencia.connect(avayaService.getActiveTerminal(), avayaService.getActiveAddress(), event.getCallNumber());

                ((CallControlCall) ligacaoConferencia).setConferenceEnable(true);
                ((CallControlCall) ligacaoConferencia).setConferenceController(avayaService.getActiveTerminal().getTerminalConnections()[1]);
                ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[1]).hold();
                ((CallControlTerminalConnection) avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();
                ((CallControlCall) avayaService.getActiveCall()).conference(ligacaoConferencia);

                logger.info("Terminal [{}] conference to {}", avayaService.getActiveTerminal().getName(), event.getCallNumber());

                //avayaService.setLigacaoConferencia( null );

                callbackDispatcher.dispatch(new CTIResponse("conference", 0, "Conference Completed.", Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>("arg1", "one"),
                        new AbstractMap.SimpleEntry<>("arg2", "two"))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
            }

        } catch (Exception e) {
            avayaService.setLigacaoConferencia( null );
            logger.error("Conference error", e);
        }
    }
}
