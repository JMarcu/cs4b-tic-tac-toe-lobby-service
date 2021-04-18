package routes;

import com.google.gson.Gson;
import interfaces.Sender;
import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

import models.ServerMessage.AuthenticationRequestMessageBody;
import models.ServerMessage.AuthenticationResultMessageBody;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;

@ServerEndpoint(value = "/ws")
public class WebsocketEndpoint implements Sender {
    Session session;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open");
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close");
    }

    @OnMessage
    public void onMessage(String messageString) {
        Gson gson = new Gson();
        Message message = gson.fromJson(messageString, Message.class);
        switch(message.getType()){
            case AUTHENTICATION_ACKNOWLEDGED:
                break;
            case AUTHENTICATION_REQUEST:
                AuthenticationRequestMessageBody authRequestBody = gson.fromJson(message.getBody(), AuthenticationRequestMessageBody.class);
                boolean validToken = authRequestBody.getToken() != "";
                AuthenticationResultMessageBody authResultResponse = new AuthenticationResultMessageBody(validToken);
                try {
                    send(new Message(authResultResponse, MessageType.AUTHENTICATION_RESULT));
                } catch (IOException e) { e.printStackTrace(); }
                break;
            case AUTHENTICATION_RESULT:
                break;
            case CHAT:
                break;
            case CONNECTION:
                break;
            case CREATE_LOBBY:
                break;
            case LOBBY_LIST:
                break;
            case MOVE:
                break;
            case PLAYER_PROPERTIES:
                break;
            case REQUEST_PLAYER:
                break;
            default:
                break;
        }
    }

    public void send(Message message) throws IOException{
        session.getBasicRemote().sendText(new Gson().toJson(message));
    }
}
