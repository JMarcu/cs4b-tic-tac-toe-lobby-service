package routes;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import interfaces.Sender;
import java.io.IOException;
import java.lang.reflect.Modifier;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

import models.Player;
import models.ServerMessage.Message;
import models.ServerMessage.MessageBody.AuthenticationRequestMessageBody;
import models.ServerMessage.MessageBody.ConnectionMessageBody;
import models.ServerMessage.MessageBody.CreateLobbyMessageBody;
import models.ServerMessage.MessageBody.MoveMessageBody;
import models.ServerMessage.MessageBody.PlayAgainMessageBody;
import models.ServerMessage.MessageBody.SpectateJoinMessageBody;
import models.ServerMessage.MessageBody.SpectateLeaveMessageBody;
import models.ServerMessage.MessageHandler.AuthenticationAcknowledgedHandler;
import models.ServerMessage.MessageHandler.AuthenticationRequestHandler;
import models.ServerMessage.MessageHandler.ConnectionHandler;
import models.ServerMessage.MessageHandler.CreateLobbyHandler;
import models.ServerMessage.MessageHandler.LobbyListHandler;
import models.ServerMessage.MessageHandler.MoveHandler;
import models.ServerMessage.MessageHandler.PlayAgainHandler;
import models.ServerMessage.MessageHandler.SpectateJoinHandler;
import models.ServerMessage.MessageHandler.SpectateLeaveHandler;
import services.GameServerService;
import services.JWTService;
import services.MessageExecutor;

@ServerEndpoint(value = "/ws")
public class WebsocketEndpoint implements Sender {
    Gson gson;
    String jwt;
    Session session;

    public WebsocketEndpoint(){
        this.gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .setExclusionStrategies(new ExclusionStrategy(){
                @Override
                public boolean shouldSkipField(FieldAttributes f){
                    boolean exclude = false;
                    try{
                        exclude = f.getName().equals("serialVersionUID");
                    } catch(Exception err){ }
                    return exclude;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open");
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close");
        DecodedJWT decodedJwt = JWTService.decode(jwt);
        System.out.println("decodedJwt: " + decodedJwt);
        String playerJson = decodedJwt.getClaim("player").asString();
        System.out.println("playerJson: " + playerJson);
        Player player = gson.fromJson(playerJson, Player.class);
        System.out.println("player: " + player);
        GameServerService.getInstance().removePlayer(player, this);
        GameServerService.getInstance().removeClient(player.getUuid());
    }

    @OnMessage
    public void onMessage(String messageString) {
        System.out.println("Message Received: " + messageString);
        Message message = gson.fromJson(messageString, Message.class);
        try{
            switch(message.getType()){
                case AUTHENTICATION_ACKNOWLEDGED:
                    AuthenticationAcknowledgedHandler authAcknowledgedHandler = new AuthenticationAcknowledgedHandler(this);
                    MessageExecutor.getInstance().queueMessageHandler(authAcknowledgedHandler);
                    break;
                case AUTHENTICATION_REQUEST:
                    AuthenticationRequestMessageBody authRequestBody = gson.fromJson(message.getBody(), AuthenticationRequestMessageBody.class);
                    jwt = authRequestBody.getToken();
                    AuthenticationRequestHandler authRequestHandler = new AuthenticationRequestHandler(authRequestBody, this, session);
                    MessageExecutor.getInstance().queueMessageHandler(authRequestHandler);
                    break;
                case AUTHENTICATION_RESULT:
                    System.out.println("Received an AUTHENTICATION_RESULT message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                case CHAT:
                    System.out.println("Received a CHAT message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                case CONNECTION:
                    ConnectionMessageBody connBody = gson.fromJson(message.getBody(), ConnectionMessageBody.class);
                    ConnectionHandler connHandler = new ConnectionHandler(connBody, this);
                    MessageExecutor.getInstance().queueMessageHandler(connHandler);
                    break;
                case CREATE_LOBBY:
                    CreateLobbyMessageBody createLobbyBody = gson.fromJson(message.getBody(), CreateLobbyMessageBody.class);
                    CreateLobbyHandler createLobbyHandler = new CreateLobbyHandler(createLobbyBody, this);
                    MessageExecutor.getInstance().queueMessageHandler(createLobbyHandler);
                    break;
                case LOBBY_LIST:
                    LobbyListHandler lobbyListHandler = new LobbyListHandler(this);
                    MessageExecutor.getInstance().queueMessageHandler(lobbyListHandler);
                    break;
                case MOVE:
                    MoveMessageBody moveBody = gson.fromJson(message.getBody(), MoveMessageBody.class);
                    MoveHandler moveHandler = new MoveHandler(moveBody);
                    MessageExecutor.getInstance().queueMessageHandler(moveHandler);
                    break;
                case PLAY_AGAIN:
                    PlayAgainMessageBody playAgainBody = gson.fromJson(message.getBody(), PlayAgainMessageBody.class);
                    PlayAgainHandler playAgainHandler = new PlayAgainHandler(playAgainBody);
                    MessageExecutor.getInstance().queueMessageHandler(playAgainHandler);
                    break;
                case PLAYER_PROPERTIES:
                    System.out.println("Received a PLAYER_PROPERTIES message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                case REQUEST_PLAYER:
                    System.out.println("Received a REQUEST_PLAYER message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                case SPECTATE_JOIN:
                    SpectateJoinMessageBody specJoinBody = gson.fromJson(message.getBody(), SpectateJoinMessageBody.class);
                    SpectateJoinHandler specJoinHandler = new SpectateJoinHandler(specJoinBody, this);
                    MessageExecutor.getInstance().queueMessageHandler(specJoinHandler);
                    break;
                case SPECTATE_LEAVE:
                    SpectateLeaveMessageBody specLeaveBody = gson.fromJson(message.getBody(), SpectateLeaveMessageBody.class);
                    SpectateLeaveHandler specLeaveHandler = new SpectateLeaveHandler(specLeaveBody);
                    MessageExecutor.getInstance().queueMessageHandler(specLeaveHandler);
                    break;
                case SPECTATE_SUCCESS:
                    System.out.println("Received a SPECTATE_SUCCESS message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                default:
                    System.out.println("Received a message of unknown type :: " + messageString);
                    break;
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(Session session){
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) throws IOException{
        String msg = gson.toJson(message);
        System.out.println("Sending Message: " + msg);
        session.getBasicRemote().sendText(msg);
    }
}
