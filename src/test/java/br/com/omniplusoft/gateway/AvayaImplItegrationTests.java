package br.com.omniplusoft.gateway;

import br.com.omniplusoft.gateway.application.ctiplatform.AbstractCTIPlatformTest;
import br.com.omniplusoft.gateway.application.ctiplatform.config.TestWebSocketConfig;
import br.com.omniplusoft.gateway.application.ctiplatform.support.StompTextMessageBuilder;
import br.com.omniplusoft.gateway.application.ctiplatform.support.TestClientWebSocketHandler;
import br.com.omniplusoft.gateway.application.ctiplatform.support.TomcatWebSocketTestServer;
import br.com.omniplusoft.gateway.domain.avaya.AvayaService;
import br.com.omniplusoft.gateway.domain.avaya.handler.*;
import br.com.omniplusoft.gateway.domain.avaya.listener.AvayaAgentListener;
import br.com.omniplusoft.gateway.domain.avaya.listener.AvayaCallControlTerminalConnectionListener;
import br.com.omniplusoft.gateway.domain.avaya.listener.AvayaProviderListener;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.*;
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

    private void initLogin() throws Exception {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setServiceName("AVAYA#PLURISMIDIA#CSTA#CTIAES");
        loginEvent.setUserAdmin("ctiuser");
        loginEvent.setPasswordAdmin("Ctiuser@001");
        loginEvent.setTerminalNumber("2409");
        loginEvent.setAgentNumber("1480");

        assertTrue(getPayloadReturnFromEvent("login", loginEvent).contains("READY"));
    }

    protected AvayaService buildExtraAvayaService(){
        AvayaService service = new AvayaService();

        service.setAvayaCallControlTerminalConnectionListener(new AvayaCallControlTerminalConnectionListener(service));
        service.setAvayaProviderListener(new AvayaProviderListener(service));
        service.setAvayaAgentListener(new AvayaAgentListener(service));

        service.setCallbackDispatcher(new CallbackDispatcher());

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

    @Test
    public void loginShouldReturnReadyMessage() throws Exception {

        initLogin();

    }

    @Test
    public void makeCallShouldReturnOKMessage() throws Exception {

        initLogin();

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("MAKECALL", new MakeCallEvent("2302")).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void answerShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void holdAndUnholShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("HOLD", new HoldEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("UNHOLD", new UnHoldEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void consultAndCancelConsultShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService userOneService = buildExtraAvayaService();
        AvayaLoginHandler userOneLoginHandler = new AvayaLoginHandler(userOneService, new CallbackDispatcher());
        userOneLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        Thread.sleep(3000);

        AvayaMakeCallHandler userOneMakeCallHandler = new AvayaMakeCallHandler(userOneService, new CallbackDispatcher());
        userOneMakeCallHandler.execute(new MakeCallEvent("2409"));

        this.TIMEOUT = 3;

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("CONSULT", new ConsultEvent("3168")).contains("MESSAGE"));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswerCallHandler = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswerCallHandler.execute(new AnswerEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("CANCELCONSULT", new CancelConsultEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void transferAfterConsultShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("CONSULT", new ConsultEvent("3168")).contains("MESSAGE"));

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(2000);


        assertTrue(getPayloadReturnFromEvent("TRANSFER", new TransferEvent()).contains("MESSAGE"));

        Thread.sleep(2000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void blindTransferShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));


        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("TRANSFER", new TransferEvent("", "3168")).contains("MESSAGE"));
        Thread.sleep(1000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));

    }

    @Test
    public void conferenceShouldReturnOKMessage() throws Exception {

        initLogin();

        Thread.sleep(3000);

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("CONFERENCE", new ConferenceEvent("3168")).contains("MESSAGE"));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswer = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswer.execute(new AnswerEvent());

        Thread.sleep(1000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));


        Thread.sleep(1000);

        AvayaDropCallHandler userOneDropTransfer = new AvayaDropCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneDropTransfer.execute(new DropCallEvent());


        Thread.sleep(1000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));

    }

}
