package br.com.omniplusoft.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hermeswaldemarin on 10/12/15.
 */
@RestController
public class CTIControllerTeste {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;


    /*
    @RequestMapping("/cti/message")
    @ResponseBody
    public OutputMessage sendMessage (@RequestBody Message message )  throws IOException {
        this.messagingTemplate.convertAndSend("/topic/ctievent." + message.getRamal(), message);
        return new OutputMessage(message, new Date());
    }
    */

    @RequestMapping("/gravaContatoteste")
    @ResponseBody
    public void gravaContato () {

        //omniPlusoftCTI.login();

    }

}
