package models.ServerMessage.MessageHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import interfaces.Sender;
import models.GameServer;
import models.Player;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MessageBody.MoveMessageBody;
import services.GameServerService;
import services.JWTService;

public class MoveHandler implements Runnable{
    MoveMessageBody msg;

    public MoveHandler(MoveMessageBody msg){
        this.msg = msg;
    }

    @Override
    public void run() {
        if(JWTService.validate(msg.getJWT())){
            DecodedJWT decodedJwt = JWTService.decode(msg.getJWT());
            Player player = new Gson().fromJson(decodedJwt.getClaim("player").asString(), Player.class);

            GameServer gameServer = GameServerService.getInstance().getGameServer(msg.getLobbyId());
            boolean success = gameServer.makeMove(player, msg.getMove());
            if(success){
                Message message = new Message(msg, MessageType.MOVE);
                GameServerService.getInstance().broadcast(msg.getLobbyId(), message);
            }
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJWT());
        }
    }
}
