package br.com.omniplusoft.gateway.avayaimpl.domain.handler;

import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIResponse;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.BecomeAvailableEvent;
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
public class AvayaBecomeAvailableHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaBecomeAvailableHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.BECOMEAVAILABLE)
    public void execute(BecomeAvailableEvent event){

        try{
            if (avayaService.getAgentLogged() != null) {
                ((LucentV6Agent)avayaService.getAgentLogged()).setState(Agent.READY, LucentAgent.MODE_AUTO_IN, 0, false);
            } else {
                logger.info("Agent not logged");
            }
        } catch (Exception e) {
            logger.error("Erro setReady - > ", e);
        }

    }
}
