package br.com.omniplusoft.gateway;

import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LogoutEvent;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.ReadyEvent;
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
public class ReadyHandlerTest extends AbstractAvayaTest{

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent(){

        handler.dispatch(new ReadyEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
