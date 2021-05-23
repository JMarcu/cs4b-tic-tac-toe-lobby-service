package models.ServerMessage.MessageHandler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import org.javatuples.Pair;

import models.Ai;
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

            GameServer gameServer = GameServerService.getInstance().getGameServer(msg.getLobbyId());
            boolean success = gameServer.makeMove(player, msg.getMove());
            if(success){
                Message message = new Message(msg, MessageType.MOVE);
                GameServerService.getInstance().broadcast(msg.getLobbyId(), message);
            }

            if(
                gameServer.getGameState().getCurrentPlayer() != null &&
                gameServer.getGameState().getCurrentPlayer().getIsAI()
            ){
                Ai aiPlayer = (Ai)gameServer.getGameState().getCurrentPlayer();
                Pair<Integer, Integer> move = aiPlayer.generateMove(gameServer.getGameState());
                boolean aiSuccess = gameServer.makeMove(player, move);
                if(aiSuccess){
                    MoveMessageBody aiBody = new MoveMessageBody("", msg.getLobbyId(), move);
                    Message message = new Message(aiBody, MessageType.MOVE);
                    GameServerService.getInstance().broadcast(msg.getLobbyId(), message);
                }
            }
        } else{
            System.err.println("Invalid JWT in Connection Request :: " + msg.getJWT());
        }
    }
}
