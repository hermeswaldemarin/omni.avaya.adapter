package br.com.plusoftomni.integration.application;

import br.com.plusoftomni.integration.domain.User;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

/**
 * Created by hermeswaldemarin on 15/01/16.
 */
@RestController
@RequestMapping("/activedirectory")
public class ActiveDirectoryController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MessageReturn gravaPostosAutorizados (@RequestBody User user) throws Exception {
        try {
            UniAddress dc = UniAddress.getByName("172.16.1.8");

            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("PLUSOFT-DM", user.getUsername(), user.getPassword());
            SmbSession.logon( dc, auth );

            return new MessageReturn("Login OK");

        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid host");
        } catch (SmbException e) {
            throw new RuntimeException("Invalid Login: " + e.getMessage(), e);
        }

    }

}
