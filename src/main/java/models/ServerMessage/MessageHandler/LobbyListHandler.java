package models.ServerMessage.MessageHandler;

import interfaces.Sender;
import java.io.IOException;
import java.util.ArrayList;
import models.Lobby;
import models.ServerMessage.Message;
import models.ServerMessage.MessageBody.LobbyListMessageBody;
import models.ServerMessage.MessageType;
import services.GameServerService;

public class LobbyListHandler implements Runnable {
    
    private Sender sender;

    public LobbyListHandler(Sender sender){
        this.sender = sender;
    }
    
    public void run(){
        try {
            final ArrayList<Lobby> lobbies = GameServerService.getInstance().listAsLobbies();
            final LobbyListMessageBody lobbyListBody = new LobbyListMessageBody(lobbies);
            sender.send(new Message(lobbyListBody, MessageType.LOBBY_LIST));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
