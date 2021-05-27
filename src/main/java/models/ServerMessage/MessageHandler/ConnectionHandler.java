package models.ServerMessage.MessageHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import interfaces.Sender;
import java.io.IOException;
import models.GameServer;
import models.Player;
import models.ServerMessage.Message;
import models.ServerMessage.MessageBody.ConnectionMessageBody;
import models.ServerMessage.MessageBody.ConnectionSuccessMessageBody;
import models.ServerMessage.MessageBody.PlayerJoinedMessageBody;
import models.ServerMessage.MessageType;
import services.GameServerService;
import services.JWTService;

public class ConnectionHandler implements Runnable{
    private ConnectionMessageBody msg;
    private Sender sender;

    public ConnectionHandler(ConnectionMessageBody msg, Sender sender){
        this.msg = msg;
        this.sender = sender;
    }
    
    public void run(){
        Message message = null;
        Player player = null;

        boolean authorized = false;
        if(JWTService.validate(msg.getJwt())){
            DecodedJWT decodedJwt = JWTService.decode(msg.getJwt());
            player = new Gson().fromJson(decodedJwt.getClaim("player").asString(), Player.class);
            if(player.getUuid().compareTo(msg.getPlayerId()) == 0){
                authorized = true;
            }
        }

        if(authorized){
            GameServer gameServer = GameServerService.getInstance().getGameServer(msg.getLobbyId());
            int position = 0;
            boolean success;
            if(msg.getType() == ConnectionMessageBody.Type.JOIN){
                success = GameServerService.getInstance().addPlayer(msg.getLobbyId(), player, sender);
                position = gameServer.getPlayers().getValue0().getUuid().equals(player.getUuid())
                   ? 0
                   : 1;
            } else{
                System.out.println("Attempting to remove player from lobby.");
                success = GameServerService.getInstance().removePlayer(msg.getLobbyId(), player, sender);
                System.out.println("success: " + success);
            }

            if(success){
                ConnectionSuccessMessageBody body;
                if(msg.getType() == ConnectionMessageBody.Type.JOIN){
                    PlayerJoinedMessageBody joinBody = new PlayerJoinedMessageBody(msg.getLobbyId(), player, position);
                    Message broadcastMsg = new Message(joinBody, MessageType.PLAYER_JOINED);
                    GameServerService.getInstance().broadcast(msg.getLobbyId(), broadcastMsg, msg.getPlayerId());

                    body = new ConnectionSuccessMessageBody(
                        gameServer.getGameState(), 
                        gameServer.getId(),
                        msg.getType()
                    );
                    message = new Message(body, MessageType.CONNECTION_SUCCESS);

                    try {
                        sender.send(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    body = new ConnectionSuccessMessageBody(null, gameServer.getId(), msg.getType());
                    message = new Message(body, MessageType.CONNECTION_SUCCESS);
                    GameServer server = GameServerService.getInstance().getGameServer(msg.getLobbyId());
                    if(server != null){
                        server.reset();
                    }
                }
            } else{
                message = new Message("", MessageType.CONNECTION_FAILURE);
                try {
                    sender.send(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJwt());
            message = new Message("", MessageType.CONNECTION_FAILURE);
            try {
                sender.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
