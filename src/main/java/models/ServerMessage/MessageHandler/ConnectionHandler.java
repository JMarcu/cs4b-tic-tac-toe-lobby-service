package models.ServerMessage.MessageHandler;

import java.io.IOException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import interfaces.Sender;
import models.GameServer;
import models.Player;
import models.ServerMessage.Message;
import models.ServerMessage.MessageBody.ConnectionMessageBody;
import models.ServerMessage.MessageBody.ConnectionSuccessMessageBody;
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

            boolean success;
            if(msg.getType() == ConnectionMessageBody.Type.JOIN){
                success = GameServerService.getInstance().addPlayer(msg.getLobbyId(), player, sender);
            } else{
                success = GameServerService.getInstance().removePlayer(msg.getLobbyId(), player, sender);
            }

            if(success){
                ConnectionMessageBody sanitizedBody = new ConnectionMessageBody("", msg.getLobbyId(), msg.getPlayerId(), msg.getType());
                Message broadcastMsg = new Message(sanitizedBody, MessageType.CONNECTION);
                GameServerService.getInstance().broadcast(msg.getLobbyId(), broadcastMsg, msg.getPlayerId());
    
                ConnectionSuccessMessageBody body;
                if(msg.getType() == ConnectionMessageBody.Type.JOIN){
                    body = new ConnectionSuccessMessageBody(gameServer.getGameState());
                } else{
                    body = new ConnectionSuccessMessageBody(null);
                }
                message = new Message(body, MessageType.CONNECTION_SUCCESS);
            } else{
                message = new Message("", MessageType.CONNECTION_FAILURE);
            }
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJwt());
            message = new Message("", MessageType.CONNECTION_FAILURE);
        }

        try {
            sender.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
