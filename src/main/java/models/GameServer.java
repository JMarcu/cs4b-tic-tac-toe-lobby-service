package models;

import java.util.UUID;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.javatuples.Pair;

public class GameServer extends Lobby {

    private GameState gameState;
    private Subscription gameStateSubscription;

    public GameServer(Lobby lobby){
        super(lobby.getId(), lobby.getName(), lobby.getPlayers(), lobby.getStatus());
        this.gameState = new GameState();
        this.subscribeToGameState();
    }

    public GameServer(String name) {
        super(name);
        this.gameState = new GameState();
        this.subscribeToGameState();
    }

    public GameServer(UUID id, String name, Pair<Player, Player> players, GameState.Status status){
        super(id, name, players, status);
        this.gameState = new GameState();
        this.subscribeToGameState();
    }

    public Lobby toLobby(){
        return new Lobby(id, name, players, status);
    }

    public GameState getGameState(){
        return gameState;
    }

    @Override
    public boolean addPlayer(Player player){
        if(this.players.getValue0() == null){
            players = new Pair<Player, Player>(player, players.getValue1());
            gameState.setPlayerOne(player);
            return true;
        } else if(this.players.getValue1() == null){
            players = new Pair<Player, Player>(players.getValue0(), player);
            gameState.setPlayerTwo(player);
            return true;
        } else{
            return false;
        }
    }

    @Override
    public boolean removePlayer(Player player){
        System.out.println("Removing Player From Lobby");
        System.out.println("Players: " + this.players);
        System.out.println("Player Values: " + this.players.getValue0() + ", " + this.players.getValue1());
        // System.out.println("Player One: " + this.players.getValue0() == null ? "null" : players.getValue0().getUuid());
        // System.out.println("Player Two: " + this.players.getValue1() == null ? "null" : players.getValue1().getUuid());
        if(this.players.getValue0() != null && this.players.getValue0().getUuid().equals(player.getUuid())){
            System.out.println("Player was Player One");
            players = new Pair<Player, Player>(null, players.getValue1());
            gameState.setPlayerOne(null);
            return true;
        } else if(this.players.getValue1() != null && this.players.getValue1().getUuid().equals(player.getUuid())){
            System.out.println("Player was Player Two");
            players = new Pair<Player, Player>(players.getValue0(), null);
            gameState.setPlayerTwo(null);
            return true;
        } else{
            return false;
        }
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

    private void subscribeToGameState(){
        if(gameStateSubscription != null){
            gameStateSubscription.cancel();
        }
        gameState.subscribe(new Subscriber<GameState.Patch>(){
            @Override public void onSubscribe(Subscription subscription) { 
                gameStateSubscription = subscription; 
                subscription.request(1);
            }
            @Override public void onNext(GameState.Patch item) { onGameStatePatch(item); };
            @Override public void onError(Throwable throwable) { }
            @Override public void onComplete() { }
        });
    }

    private void onGameStatePatch(GameState.Patch patch){
        if(patch.getStatus() != null){
            status = patch.getStatus();
        }
    }
}
