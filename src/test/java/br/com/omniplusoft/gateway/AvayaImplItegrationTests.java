package br.com.omniplusoft.gateway;

import br.com.omniplusoft.gateway.application.ctiplatform.AbstractCTIPlatformTest;
import br.com.omniplusoft.gateway.application.ctiplatform.config.TestWebSocketConfig;
import br.com.omniplusoft.gateway.application.ctiplatform.support.StompTextMessageBuilder;
import br.com.omniplusoft.gateway.application.ctiplatform.support.TestClientWebSocketHandler;
import br.com.omniplusoft.gateway.application.ctiplatform.support.TomcatWebSocketTestServer;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.CTIEvent;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LoginEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.servlet.Filter;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class AvayaImplItegrationTests extends AbstractCTIPlatformTest {

    public AvayaImplItegrationTests(){
        this.activeProfile = "";
        this.TIMEOUT = 10;
    }

    @Test
    public void loginShouldReturnOkMessage() throws Exception {

        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setServiceName("AVAYA#PLURISMIDIA#CSTA#CTIAES");
        loginEvent.setUserAdmin("ctiuser");
        loginEvent.setPasswordAdmin("Ctiuser@001");
        loginEvent.setTerminalNumber("2409");
        loginEvent.setAgentNumber("1480");

        assertTrue(getPayloadReturnFromEvent("login", loginEvent).contains("READY"));

    }

}
