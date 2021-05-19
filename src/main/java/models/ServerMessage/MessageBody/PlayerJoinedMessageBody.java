package models.ServerMessage.MessageBody;

import java.util.UUID;
import models.Player;

public class PlayerJoinedMessageBody {
    private UUID lobbyId;
    private Player player;

    public PlayerJoinedMessageBody(UUID lobbyId, Player player){
        this.lobbyId = lobbyId;
        this.player = player;
    }

    public UUID getLobbyId(){ return lobbyId; }
    public Player getPlayer(){ return this.player; }
}
