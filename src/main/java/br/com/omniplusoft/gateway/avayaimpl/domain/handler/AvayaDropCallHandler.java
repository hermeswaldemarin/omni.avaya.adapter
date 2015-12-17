package br.com.omniplusoft.gateway.avayaimpl.domain.handler;

import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.DropCallEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.TerminalConnection;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaDropCallHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaDropCallHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.DROPCALL)
    public void execute(DropCallEvent event){

        try {

            if (avayaService.getActiveTerminal() != null)  {
                if ((avayaService.getActiveTerminal().getTerminalConnections()) != null) {
                    logger.trace("Active Connections {}", avayaService.getActiveTerminal().getTerminalConnections());
                    TerminalConnection terminalConnection = avayaService.getActiveTerminal().getTerminalConnections()[0];
                    logger.trace("Terminal [{}] Ligacao Finalizada", avayaService.getActiveTerminal().getName());
                    logger.trace("Terminal Connection", terminalConnection);
                    if (terminalConnection != null) {
                        logger.trace("Terminal Connection is null");
                        terminalConnection.getConnection().disconnect();
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Drop Error", e);
        }
    }
}
