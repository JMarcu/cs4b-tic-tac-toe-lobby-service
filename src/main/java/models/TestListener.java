package models;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.util.json.ParseException;

import models.ServerMessage.AuthenticationResultMessageBody;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;

@ServerEndpoint("/ws")
public class TestListener {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open");
    }


    @OnClose
    public void onClose(Session session) {
        System.out.println("close");
    }

    @OnMessage
    public String onMessage(String messageString) {
        System.out.println("Received Message: " + messageString);
        Message message = new Message();
        try {
            message.fromJSONString(messageString);
            boolean isAuthRequest = message.getType() == MessageType.AUTHENTICATION_REQUEST;
            System.out.println("AuthenticationRequest? " + isAuthRequest);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        AuthenticationResultMessageBody responseBody = new AuthenticationResultMessageBody(true);
        Message response = new Message(responseBody, MessageType.AUTHENTICATION_RESULT);
        return response.toJSONString();
    }
}
