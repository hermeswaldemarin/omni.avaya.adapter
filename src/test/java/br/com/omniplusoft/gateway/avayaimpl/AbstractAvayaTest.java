package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.avayaimpl.domain.listener.AvayaAgentListener;
import br.com.omniplusoft.gateway.avayaimpl.domain.listener.AvayaCallControlTerminalConnectionListener;
import br.com.omniplusoft.gateway.avayaimpl.domain.listener.AvayaProviderListener;
import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
public abstract class AbstractAvayaTest {

    protected LoginEvent loginEvent;

    @Autowired
    CallbackDispatcher callbackDispatcher;

    public void initLoginEvent(){

        loginEvent = new LoginEvent();
        loginEvent.setServiceName("AVAYA#PLURISMIDIA#CSTA#CTIAES");
        loginEvent.setUserAdmin("ctiuser");
        loginEvent.setPasswordAdmin("Ctiuser@001");
        loginEvent.setTerminalNumber("2409");
        loginEvent.setAgentNumber("1480");

    }

    protected AvayaService buildExtraAvayaService(){
        AvayaService service = new AvayaService();

        service.setAvayaCallControlTerminalConnectionListener(new AvayaCallControlTerminalConnectionListener(service));
        service.setAvayaProviderListener(new AvayaProviderListener(service));
        service.setAvayaAgentListener(new AvayaAgentListener(service));

        service.setCallbackDispatcher(callbackDispatcher);

        return service;
    }

    protected LoginEvent initLoginEventExtraService(String terminal, String agent){

        LoginEvent loginEventSecondAgent = new LoginEvent();
        loginEventSecondAgent.setServiceName("AVAYA#PLURISMIDIA#CSTA#CTIAES");
        loginEventSecondAgent.setUserAdmin("ctiuser");
        loginEventSecondAgent.setPasswordAdmin("Ctiuser@001");
        loginEventSecondAgent.setTerminalNumber(terminal);
        loginEventSecondAgent.setAgentNumber(agent);

        return loginEventSecondAgent;

    }

}
