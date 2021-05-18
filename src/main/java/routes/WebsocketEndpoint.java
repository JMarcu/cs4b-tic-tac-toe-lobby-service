package routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import interfaces.Sender;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
import models.ServerMessage.MessageHandler.AuthenticationAcknowledgedHandler;
import models.ServerMessage.MessageHandler.AuthenticationRequestHandler;
import models.ServerMessage.MessageHandler.ConnectionHandler;
import models.ServerMessage.MessageHandler.CreateLobbyHandler;
import models.ServerMessage.MessageHandler.LobbyListHandler;
import models.ServerMessage.MessageHandler.MoveHandler;
import org.javatuples.Pair;
import services.MessageExecutor;

@ServerEndpoint(value = "/ws")
public class WebsocketEndpoint implements Sender {
    Gson gson;
    Session session;

    public WebsocketEndpoint(){
        final JsonSerializer<Pair<Player, Player>> playerPairSerializer = new JsonSerializer<Pair<Player, Player>>(){
            @Override
            public JsonElement serialize(Pair<Player, Player> src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray jsonPair = new JsonArray();
                jsonPair.add(WebsocketEndpoint.this.gson.toJson(src.getValue0()));
                jsonPair.add(WebsocketEndpoint.this.gson.toJson(src.getValue1()));
                return jsonPair;
            }
            
        };

        final JsonDeserializer<Pair<Player, Player>> playerPairDerializer = new JsonDeserializer<Pair<Player, Player>>(){
            @Override
            public Pair<Player, Player> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return Pair.fromArray(
                    WebsocketEndpoint.this.gson.fromJson(json, typeOfT)
                );
            }
        };
        
        this.gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .registerTypeAdapter(
                new TypeToken<Pair<Player, Player>>(){}.getType(), 
                playerPairSerializer
            )
            .registerTypeAdapter(
                new TypeToken<Pair<Player, Player>>(){}.getType(), 
                playerPairDerializer
            )
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
                case PLAYER_PROPERTIES:
                    System.out.println("Received a PLAYER_PROPERTIES message :: this is likely an error, the lobby service does not process this message type.");
                    break;
                case REQUEST_PLAYER:
                    System.out.println("Received a REQUEST_PLAYER message :: this is likely an error, the lobby service does not process this message type.");
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
