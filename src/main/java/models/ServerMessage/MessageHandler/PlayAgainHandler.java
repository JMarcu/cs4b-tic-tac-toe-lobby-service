package models.ServerMessage.MessageHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import models.GameServer;
import models.Player;
import models.ServerMessage.MessageBody.PlayAgainMessageBody;
import services.GameServerService;
import services.JWTService;

public class PlayAgainHandler implements Runnable{
    private PlayAgainMessageBody msg;

    public PlayAgainHandler(PlayAgainMessageBody msg){
        this.msg = msg;
    }
    
    public void run(){
        if(JWTService.validate(msg.getJwt())){
            DecodedJWT decodedJwt = JWTService.decode(msg.getJwt());
            Player player = new Gson().fromJson(decodedJwt.getClaim("player").asString(), Player.class);

            GameServer gameServer = GameServerService.getInstance().getGameServer(msg.getLobbyId());
            gameServer.playAgain(player);
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJwt());
        }
    }
}
