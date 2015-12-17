package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.CtiPlatformApplication;
import br.com.omniplusoft.gateway.domain.ctiplatform.CTIEventHandler;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.BecomeAvailableEvent;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.BecomeUnavailableEvent;
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
public class BecomeUnavailableHandlerTest extends AbstractAvayaTest{

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        handler.dispatch(new BecomeAvailableEvent());

        Thread.sleep(1000);

        handler.dispatch(new BecomeUnavailableEvent("1"));

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
