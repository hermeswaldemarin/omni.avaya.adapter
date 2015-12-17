package br.com.omniplusoft.gateway;

import br.com.omniplusoft.gateway.domain.avaya.handler.AvayaLoginHandler;
import br.com.omniplusoft.gateway.domain.avaya.handler.AvayaMakeCallHandler;
import br.com.omniplusoft.gateway.domain.avaya.AvayaService;
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
public class AnswerHandlerTest extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService("2302", "1488"));

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent("2409"));

        Thread.sleep(3000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }


}
