package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.CtiPlatformApplication;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.LogoutEvent;
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
public class LoginHandlerTest extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();
    }

    @Test
    public void shouldReceiveALoginEvent(){

        handler.dispatch(loginEvent);

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
