package models.ServerMessage.MessageHandler;

import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MessageBody.SpectateLeaveMessageBody;
import services.GameServerService;

public class SpectateLeaveHandler implements Runnable {
    SpectateLeaveMessageBody msg;

    public SpectateLeaveHandler(SpectateLeaveMessageBody msg){
        this.msg = msg;
    }
    
    public void run(){
        GameServerService.getInstance().removeSpectator(msg.getLobbyId(), msg.getPlayerId());
        GameServerService.getInstance().broadcast(
            msg.getLobbyId(), 
            new Message(msg, MessageType.SPECTATE_LEAVE),
            msg.getPlayerId()
        );
    }
}
