package br.com.omniplusoft.gateway.domain.avaya.handler;

import br.com.omniplusoft.gateway.domain.avaya.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIResponse;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.UnHoldEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.callcontrol.CallControlTerminalConnection;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaUnholdHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaUnholdHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.UNHOLD)
    public void execute(UnHoldEvent event){
        try {

            if (avayaService.getActiveTerminal() != null)  {
                if ((avayaService.getActiveTerminal().getTerminalConnections()) != null) {

                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();

                    logger.info("Terminal [{}] UnHold", avayaService.getActiveTerminal().getName());
                }
            }
        } catch (Exception e) {
            logger.error("Erro unHold - >", e);
        }

        callbackDispatcher.dispatch(new CTIResponse("unhold", 0, "Unhold call.", Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("arg1", "one"),
                new AbstractMap.SimpleEntry<>("arg2", "two"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
    }
}
