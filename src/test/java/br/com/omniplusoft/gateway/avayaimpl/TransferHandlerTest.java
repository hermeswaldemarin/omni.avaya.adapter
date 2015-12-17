package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.CtiPlatformApplication;
import br.com.omniplusoft.gateway.avayaimpl.domain.*;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtiPlatformApplication.class)
public class TransferHandlerTest  extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init() {
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldTransferAfterConsult() throws Exception {
        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(3000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new ConsultEvent("3168"));

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, callbackDispatcher);
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new TransferEvent());

        Thread.sleep(1000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, callbackDispatcher);
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, callbackDispatcher);
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, callbackDispatcher);
        userOneLogout.execute(new LogoutEvent());


    }

    @Test
    public void shouldBlindTransfer() throws Exception {
        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(3000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new TransferEvent("", "3168"));

        Thread.sleep(1000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, callbackDispatcher);
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, callbackDispatcher);
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, callbackDispatcher);
        userOneLogout.execute(new LogoutEvent());


    }

    @After
    public void destroy() {
        handler.dispatch(new LogoutEvent());
    }
}
