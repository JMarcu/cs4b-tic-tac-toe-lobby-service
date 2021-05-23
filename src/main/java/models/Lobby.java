package models;

import java.util.UUID;
import org.javatuples.Pair;

public class Lobby {
    protected boolean aiLobby;
    protected UUID id;
    protected String name;
    protected Pair<Player, Player> players;
    protected GameState.Status status;

    public Lobby(String name, boolean isAi){
        this(UUID.randomUUID(), name, new Pair<Player, Player>(null, null), GameState.Status.NEW, isAi);
    }

    public Lobby(UUID id, String name, Pair<Player, Player> players, GameState.Status status, boolean aiLobby){
        this.aiLobby = aiLobby;
        this.id = id;
        this.name = name;
        this.players = players;
        this.status = status;
    }

    public boolean addPlayer(Player player){
        if(players.getValue0() == null){
            players.setAt0(player);
            return true;
        } else if(players.getValue1() == null){
            players.setAt1(player);
            return true;
        } else{
            return false;
        }
    }

    public boolean addPlayers(Player playerOne, Player playerTwo){
        if(players.getValue0() == null && players.getValue1() == null){
            players = new Pair<Player, Player>(playerOne, playerTwo);
            return true;
        } else{
            return false;
        }
    }

    public boolean hasPlayer(UUID playerId){
        return (this.players.getValue0() != null && this.players.getValue0().getUuid().equals(playerId)) ||
            (this.players.getValue1() != null && this.players.getValue1().getUuid().equals(playerId));
    }

    public boolean removePlayer(Player player){
        if(players.getValue0().getUuid().compareTo(player.getUuid()) == 0){
            players = new Pair<Player, Player>(null, players.getValue1());
            return true;
        } else if(players.getValue1().getUuid().compareTo(player.getUuid()) == 0){
            players = new Pair<Player, Player>(players.getValue1(), null);
            return true;
        } else{
            return false;
        }
    }
    
    public UUID getId(){ return this.id; }
    public String getName(){ return this.name; }
    public Pair<Player, Player> getPlayers(){ return this.players; }
    public GameState.Status getStatus(){ return this.status; }
    public boolean isAiLobby(){ return aiLobby; }
}
