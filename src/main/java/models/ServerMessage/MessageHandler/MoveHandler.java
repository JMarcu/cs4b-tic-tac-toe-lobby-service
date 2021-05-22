package models.ServerMessage.MessageHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
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
        System.out.println("msg.getJWT(): " + msg.getJWT());
        System.out.println("JWTService.validate(msg.getJWT()): " + JWTService.validate(msg.getJWT()));

        if(JWTService.validate(msg.getJWT())){
            DecodedJWT decodedJwt = JWTService.decode(msg.getJWT());
            Player player = new Gson().fromJson(decodedJwt.getClaim("player").asString(), Player.class);
            System.out.println("player: " + player);
            System.out.println("msg.getLobbyId(): " + msg.getLobbyId());

            GameServer gameServer = GameServerService.getInstance().getGameServer(msg.getLobbyId());
            System.out.println("gameServer: " + gameServer);
            boolean success = gameServer.makeMove(player, msg.getMove());
            System.out.println("success: " + success);
            if(success){
                Message message = new Message(msg, MessageType.MOVE);
                GameServerService.getInstance().broadcast(msg.getLobbyId(), message);
            }
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJWT());
        }
    }
}
