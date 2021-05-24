package models.ServerMessage.MessageHandler;

import java.io.IOException;

import interfaces.Sender;
import models.GameServer;
import models.GameState;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MessageBody.SpectateJoinMessageBody;
import models.ServerMessage.MessageBody.SpectateSuccessMessageBody;
import services.GameServerService;

public class SpectateJoinHandler implements Runnable {
    SpectateJoinMessageBody msg;
    Sender sender;

    public SpectateJoinHandler(SpectateJoinMessageBody msg, Sender sender){
        this.msg = msg;
        this.sender = sender;
    }
    
    public void run(){
        GameServerService.getInstance().addSpectator(msg.getLobbyId(), msg.getPlayerId(), sender);

        GameServer server = GameServerService.getInstance().getGameServer(msg.getLobbyId());
        GameState gameState = server.getGameState();
        SpectateSuccessMessageBody body = new SpectateSuccessMessageBody(gameState, server.getId());
        Message successMessage = new Message(body, MessageType.SPECTATE_SUCCESS);

        try {
            sender.send(successMessage);
        
            GameServerService.getInstance().broadcast(
                msg.getLobbyId(), 
                new Message(msg, MessageType.SPECTATE_JOIN),
                msg.getPlayerId()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
