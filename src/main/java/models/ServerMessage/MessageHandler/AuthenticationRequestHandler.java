package models.ServerMessage.MessageHandler;

import interfaces.Sender;
import java.io.IOException;
import javax.websocket.Session;
import models.ServerMessage.Message;
import models.ServerMessage.MessageBody.AuthenticationRequestMessageBody;
import models.ServerMessage.MessageBody.AuthenticationResultMessageBody;
import models.ServerMessage.MessageType;
import services.JWTService;

/** 
 * This class handles validation of incoming JSON Web Tokens (JWTs) by calling the validate method
 * from the JWTService class. The class then sends the result of the validation through the sender object
 * and with a new message type of AUTHENTICATION_RESULT.
 */
public class AuthenticationRequestHandler implements Runnable{

    private AuthenticationRequestMessageBody msg;
    private Sender sender;
    private Session session;

    public AuthenticationRequestHandler(AuthenticationRequestMessageBody msg, Sender sender, Session session){
        this.msg = msg;
        this.sender = sender;
        this.session = session;
    }
    
    public void run(){
        final boolean valid = JWTService.validate(msg.getToken());

        try {
            final AuthenticationResultMessageBody authResultBody = new AuthenticationResultMessageBody(valid);
            sender.send(new Message(authResultBody, MessageType.AUTHENTICATION_RESULT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!valid){
            sender.close(session);
        }
    }
}