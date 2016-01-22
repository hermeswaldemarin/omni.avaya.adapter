package br.com.omniplusoft.gateway.application;

/**
 * Created by hermeswaldemarin on 18/01/16.
 */
public class MessageReturn {
    private String message;

    public MessageReturn(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
