package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.CtiPlatformApplication;
import br.com.omniplusoft.gateway.avayaimpl.domain.handler.AvayaAnswerHandler;
import br.com.omniplusoft.gateway.avayaimpl.domain.handler.AvayaLoginHandler;
import br.com.omniplusoft.gateway.avayaimpl.domain.handler.AvayaMakeCallHandler;
import br.com.omniplusoft.gateway.avayaimpl.domain.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtiPlatformApplication.class)
public class ConsultAndCancelConsultHandlerTest extends AbstractAvayaTest{

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        AvayaService userOneService = buildExtraAvayaService();
        AvayaLoginHandler userOneLoginHandler = new AvayaLoginHandler(userOneService, callbackDispatcher);
        userOneLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService("3168", "1478"));

        AvayaMakeCallHandler userOneMakeCallHandler = new AvayaMakeCallHandler(userOneService, callbackDispatcher);
        userOneMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(2000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new ConsultEvent("3168"));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswerCallHandler = new AvayaAnswerHandler(userTwoService, callbackDispatcher);
        userTwoAnswerCallHandler.execute(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new CancelConsultEvent());

        Thread.sleep(1000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
