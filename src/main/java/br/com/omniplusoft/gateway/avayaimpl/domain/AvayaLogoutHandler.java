package br.com.omniplusoft.gateway.avayaimpl.domain;

import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LogoutEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.telephony.CallListener;
import javax.telephony.ProviderListener;
import javax.telephony.callcenter.Agent;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaLogoutHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaLogoutHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGOUT)
    public void execute(LogoutEvent event){
        try{

            if (avayaService.getAgentLogged() != null) {

                Agent a = avayaService.getAgentLogged();
                String agentId = a.getAgentID();

                logger.trace("Agend ID {} to be logged out", agentId);

                a.getAgentTerminal().removeAgent(a);

            } else {
                logger.trace("Agent is not logged in");
            }

            CallListener[] callListenerArray = null;
            ProviderListener[] providerListener = null;

            if (avayaService.getActiveTerminal() != null) {
                if ((callListenerArray = avayaService.getActiveTerminal().getCallListeners()) != null) {

                    for( CallListener listener : callListenerArray){
                        avayaService.getActiveTerminal().removeCallListener(listener);

                        if(listener == null){
                            break;
                        }
                    }
                }

            }
            logger.trace("Shutting down the provider");
            avayaService.getProvider().shutdown();
            logger.trace("Provider is down");

            avayaService.setProvider(null);
            avayaService.setActiveTerminal(null);
            avayaService.setActiveCall(null);
            avayaService.setActiveAddress(null);

            logger.trace("The Avaya Integration is down.");


        }catch (Exception e) {
            logger.error("Logout Error", e);
        }
    }
}
