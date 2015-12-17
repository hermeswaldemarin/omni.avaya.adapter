package br.com.omniplusoft.gateway.avayaimpl;

import br.com.omniplusoft.gateway.application.ctiplatform.AbstractCTIPlatformTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AvayaImplItegrationTests extends AbstractCTIPlatformTest {

    public AvayaImplItegrationTests(){
        this.activeProfile = "avaya";
    }

    @Test
    public void loginShouldReturnOkMessage() throws Exception {

        assertTrue(getPayloadReturnFromEvent("login").contains("Login OK"));

    }

}
