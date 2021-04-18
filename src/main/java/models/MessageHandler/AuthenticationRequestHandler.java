package models.MessageHandler;

import java.io.IOException;

import Interfaces.Sender;
import models.ServerMessage.AuthenticationRequestMessageBody;
import models.ServerMessage.AuthenticationResultMessageBody;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;

public class AuthenticationRequestHandler implements Runnable {

    private AuthenticationRequestMessageBody request;
    private Sender sender;

    AuthenticationRequestHandler(AuthenticationRequestMessageBody request, Sender sender){
        this.request = request;
        this.sender = sender;
    }

    @Override
    public void run() {
        boolean validToken = request.getToken() != "";
        
        AuthenticationResultMessageBody response = new AuthenticationResultMessageBody(validToken);
        Message message = new Message(response, MessageType.AUTHENTICATION_RESULT);
        try {
            sender.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
