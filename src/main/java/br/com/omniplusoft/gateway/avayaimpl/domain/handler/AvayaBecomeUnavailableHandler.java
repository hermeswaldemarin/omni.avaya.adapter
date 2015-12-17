package br.com.omniplusoft.gateway.avayaimpl.domain.handler;

import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.BecomeUnavailableEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAgent;
import com.avaya.jtapi.tsapi.LucentV6Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.callcenter.Agent;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaBecomeUnavailableHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaBecomeUnavailableHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.BECOMEUNAVAILABLE)
    public void execute(BecomeUnavailableEvent event){
        try {
            if (avayaService.getAgentLogged() != null) {
                try {
                    ((LucentV6Agent)avayaService.getAgentLogged()).setState(Agent.NOT_READY, LucentAgent.MODE_NONE, Integer.parseInt(event.getReasonCode()), true);

                } catch (Exception e) {
                    logger.error("Erro become unavaiable - > ", e);
                }
            } else {
                logger.info("Agent not logged");
            }
        } catch (Exception e) {
            logger.error("Erro become unavaiable - > ", e);
        }
    }
}
