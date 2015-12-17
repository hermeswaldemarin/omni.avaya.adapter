package br.com.omniplusoft.gateway.avayaimpl.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.telephony.ProviderEvent;
import javax.telephony.ProviderListener;

@Service
@Lazy
public class AvayaProviderListener implements ProviderListener {

    private AvayaService avayaService;
    private Object sigProvider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaProviderListener(AvayaService avayaService){
        this.avayaService = avayaService;
    }

	public void providerEventTransmissionEnded(ProviderEvent e) {
        logger.info("providerEventTransmissionEnded");
	}

	
	public void providerInService(ProviderEvent e) {
        synchronized (sigProvider) {
            sigProvider.notify();
        }
        logger.info("providerInService");
	}

	
	public void providerOutOfService(ProviderEvent e) {
        logger.info("providerOutOfService");
	}

	
	public void providerShutdown(ProviderEvent e) {
        logger.info("providerOutOfService");
	}

    public Object getSigProvider() {
        return sigProvider;
    }

    public void setSigProvider(Object sigProvider) {
        this.sigProvider = sigProvider;
    }
}
