package br.com.omniplusoft.ctiplatform.avayaimpl;

import br.com.omniplusoft.ctiplatform.domain.OmniPlusoftCTI;
import br.com.omniplusoft.ctiplatform.infrastructure.exception.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by hermeswaldemarin on 10/12/15.
 */
@Service
public class CTIAvayaImpl implements OmniPlusoftCTI{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void login() throws LoginException {
        logger.info("Login called again");
    }
}
