package br.com.omniplusoft.gateway.avayaimpl.domain;

import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.ConsultEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentCall;
import com.avaya.jtapi.tsapi.LucentTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.Call;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.callcontrol.CallControlTerminalConnection;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaConsultHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaConsultHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.CONSULT)
    public void execute(ConsultEvent event){
        try {
            if (avayaService.getActiveCall() == null) {
                logger.info("Needed to be in a active call");
                return;
            }

            logger.trace("Enabling transfer");
            ((CallControlCall)avayaService.getActiveCall()).setTransferEnable(true);
            logger.trace("Holding the first connection");
            ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).hold();
            logger.trace("Creating the new call");
            Call consultCall = avayaService.getProvider().createCall();
            logger.trace("New Call created - Terminal : {}",event.getCallNumber());

            ((LucentCall)consultCall).connect((LucentTerminal)avayaService.getActiveTerminal(),
                    (LucentAddress)avayaService.getActiveAddress(),  event.getCallNumber(), false, null);
            logger.trace("Enabling transfer on the new call created");
            ((CallControlCall)consultCall).setTransferEnable(true);
            logger.trace("Enabling conference on the new call created");
            ((CallControlCall)consultCall).setConferenceEnable(true);
            logger.trace("Transferig the control");
            ((CallControlCall)consultCall).setTransferController(avayaService.getActiveTerminal().getTerminalConnections()[1]);

            logger.trace("Terminal [{}] Consult to {}", avayaService.getActiveTerminal().getName(),  event.getCallNumber());

            avayaService.setConsultCall(consultCall);

        } catch (Exception e) {
            logger.error("Consult Error", e);
        }
    }
}
