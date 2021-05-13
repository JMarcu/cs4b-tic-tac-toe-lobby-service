package models;

import java.util.UUID;
import org.javatuples.Pair;

public class Lobby {    
    protected UUID id;
    protected String name;
    protected Pair<Player, Player> players;
    protected GameState.Status status;

    public Lobby(String name){
        this(UUID.randomUUID(), name, new Pair<Player, Player>(null, null), GameState.Status.NEW);
    }

    public Lobby(UUID id, String name, Pair<Player, Player> players, GameState.Status status){
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
}
