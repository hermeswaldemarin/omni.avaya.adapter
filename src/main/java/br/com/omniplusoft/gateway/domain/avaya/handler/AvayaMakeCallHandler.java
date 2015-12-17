package br.com.omniplusoft.gateway.domain.avaya.handler;

import br.com.omniplusoft.gateway.domain.avaya.AvayaService;
import br.com.omniplusoft.gateway.domain.ctiplatform.CallbackDispatcher;
import br.com.omniplusoft.gateway.domain.ctiplatform.event.MakeCallEvent;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.CTIEvents;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.EventHandler;
import br.com.omniplusoft.gateway.infrastructure.ctiplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentCall;
import com.avaya.jtapi.tsapi.LucentTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.Call;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaMakeCallHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaMakeCallHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.MAKECALL)
    public void execute(MakeCallEvent event){

        synchronized (this){
            try {

                if (avayaService.getActiveAddress() == null ) {
                    throw new RuntimeException("Terminal need to be monitored.");
                }

                logger.trace("Creating the new call object");

                Call call = avayaService.getProvider().createCall();

                logger.trace("Call instantiated");

                ((LucentCall)call).connect((LucentTerminal)avayaService.getActiveTerminal(), (LucentAddress)avayaService.getActiveAddress(), event.getCallNumber(), false, null);

                logger.trace("Call created");

                avayaService.setActiveCall(call);

            } catch (Exception e) {
                throw  new RuntimeException("MakeCall Error" + e.getMessage());
            }
        }
    }
}
