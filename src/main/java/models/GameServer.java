package models;

import java.util.UUID;
import org.javatuples.Pair;

public class GameServer extends Lobby {

    private GameState gameState;

    public GameServer(Lobby lobby){
        super(lobby.getId(), lobby.getName(), lobby.getPlayers(), lobby.getStatus());
        this.gameState = new GameState();
    }

    public GameServer(String name) {
        super(name);
        this.gameState = new GameState();
    }

    public GameServer(UUID id, String name, Pair<Player, Player> players, GameState.Status status){
        super(id, name, players, status);
        this.gameState = new GameState();
    }

    public GameState getGameState(){
        return gameState;
    }

    public boolean makeMove(Player player, Pair<Integer, Integer> move){
        if(players.getValue0().equals(player) || players.getValue1().equals(player)){
            if(gameState.getCurrentPlayer().equals(player)){
                try{
                    gameState.setCell(move.getValue0(), move.getValue1());
                    return true;
                } catch(IllegalArgumentException ex){
                    ex.printStackTrace();
                    return false;
                }
            } else{
                return false;
            }
        } else{
            return false;
        }
    }
}
