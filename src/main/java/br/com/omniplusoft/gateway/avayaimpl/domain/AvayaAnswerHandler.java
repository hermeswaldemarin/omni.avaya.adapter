package br.com.omniplusoft.gateway.avayaimpl.domain;

import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.AnswerEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.Connection;
import javax.telephony.TerminalConnection;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaAnswerHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaAnswerHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.ANSWER)
    public void execute(AnswerEvent event){

        try{

            Connection[] connections = avayaService.getActiveAddress().getConnections();

            logger.trace("Active Connections : {}", connections.length);

            TerminalConnection callTerminalConnection = null;
            if (connections != null) {
                for( Connection conn: connections ){

                    logger.trace("Address Name : {} , Active Address Name {}", conn.getAddress().getName(), avayaService.getActiveAddress().getName());

                    if (conn.getAddress().getName().equals(avayaService.getActiveAddress().getName())){
                        for( TerminalConnection terminalConnection: conn.getTerminalConnections() ){

                            logger.trace("Terminal Name : {} , Active Terminal Name {}", terminalConnection.getTerminal().getName(), avayaService.getActiveTerminal().getName());

                            if (avayaService.getActiveTerminal().getName().equals(terminalConnection.getTerminal().getName())) {
                                callTerminalConnection = terminalConnection;
                                break;
                            }
                        }
                    }
                }

                final TerminalConnection conn = callTerminalConnection;
                Runnable task2 = () -> {
                    try {
                        if (conn != null) {
                            conn.answer();
                        } else {
                            logger.info("Terminal Connection Não Encontrado");
                        }
                    } catch (Exception e) {
                        logger.error("Erro Atender - >", e);
                    }

                };

                new Thread(task2).start();

            }
        }catch (Exception e) {
            logger.error("Erro Atender - >", e);
        }

    }
}
