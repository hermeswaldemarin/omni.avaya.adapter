package br.com.omniplusoft.gateway.avayaimpl.domain.handler;

import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.HoldEvent;
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
public class AvayaHoldHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaHoldHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.HOLD)
    public void execute(HoldEvent event){
        try {

            if (avayaService.getActiveTerminal() != null)  {
                if ((avayaService.getActiveTerminal().getTerminalConnections()) != null) {

                    logger.trace("Set Terminal [{}] on hold", avayaService.getActiveTerminal().getName());

                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).hold();

                    logger.trace("Terminal [{}] Hold setted", avayaService.getActiveTerminal().getName());

                }
            }
        } catch (Exception e) {
            logger.error("Hold Error", e);
        }
    }
}
