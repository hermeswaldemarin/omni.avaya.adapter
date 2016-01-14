package br.com.omniplusoft.gateway;

import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.DropCallEvent;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LogoutEvent;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.MakeCallEvent;
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
@SpringApplicationConfiguration(classes = OmniPlusoftGatewayPlatformApplication.class)
public class MakeCallHandlerTest extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){

        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{


        handler.dispatch(new MakeCallEvent("2302"));

        Thread.sleep(1000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
