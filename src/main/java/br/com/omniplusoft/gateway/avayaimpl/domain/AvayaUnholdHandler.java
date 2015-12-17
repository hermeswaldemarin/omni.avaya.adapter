package br.com.omniplusoft.gateway.avayaimpl.domain;

import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.UnHoldEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.callcontrol.CallControlTerminalConnection;

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
    }
}
