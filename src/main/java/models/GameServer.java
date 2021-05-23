package models;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.javatuples.Pair;

import javafx.scene.paint.Color;
import models.GameState.Status;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MessageBody.NewGameMessageBody;
import services.GameServerService;

public class GameServer extends Lobby {

    private GameState gameState;
    private Subscription gameStateSubscription;
    private boolean playerOnePlayAgain;
    private boolean playerTwoPlayAgain;

    public GameServer(Lobby lobby){
        super(lobby.getId(), lobby.getName(), lobby.getPlayers(), lobby.getStatus(), lobby.isAiLobby());
        this.gameState = new GameState();
        this.subscribeToGameState();
        if(aiLobby){
            this.initializeAi();
        }
    }

    public GameServer(String name, boolean isAi) {
        super(name, isAi);
        this.gameState = new GameState();
        this.subscribeToGameState();
        if(aiLobby){
            this.initializeAi();
        }
    }

    public GameServer(UUID id, String name, Pair<Player, Player> players, GameState.Status status, boolean aiLobby){
        super(id, name, players, status, aiLobby);
        this.gameState = new GameState();
        this.subscribeToGameState();
        if(aiLobby){
            this.initializeAi();
        }
    }

    public Lobby toLobby(){
        return new Lobby(id, name, players, status, aiLobby);
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
        if(this.players.getValue0() != null && this.players.getValue0().getUuid().equals(player.getUuid())){
            players = new Pair<Player, Player>(null, players.getValue1());
            gameState.setPlayerOne(null);
            return true;
        } else if(this.players.getValue1() != null && this.players.getValue1().getUuid().equals(player.getUuid())){
            players = new Pair<Player, Player>(players.getValue0(), null);
            gameState.setPlayerTwo(null);
            return true;
        } else{
            return false;
        }
    }

    public boolean makeMove(Player player, Pair<Integer, Integer> move){
        System.out.println("player" + player);
        System.out.println("gameState.getCurrentPlayer()" + gameState.getCurrentPlayer());
        System.out.println("gameState.getStatus()" + gameState.getStatus());
        System.out.println("gameState.getCurrentPlayer().getUuid()" + gameState.getCurrentPlayer().getUuid());
        System.out.println("player.getUuid()" + player.getUuid());
        if(
            player != null &&
            gameState.getCurrentPlayer() != null && 
            gameState.getStatus() == GameState.Status.IN_PROGRESS &&
            gameState.getCurrentPlayer().getUuid().equals(player.getUuid())
        ){
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
    }

    public void playAgain(Player player){
        if(
            players.getValue0() == null ||
            player.getUuid().equals(players.getValue0().getUuid())
        ){
                playerOnePlayAgain = true;
        }

        if(
            players.getValue1() == null ||
            player.getUuid().equals(players.getValue1().getUuid())
        ){
                playerTwoPlayAgain = true;
        }

        if(playerOnePlayAgain && playerTwoPlayAgain){
            playerOnePlayAgain = false;
            playerTwoPlayAgain = false;

            this.gameState = new GameState(
                gameState.getGameMode(),
                gameState.getPlayers(),
                gameState.getSinglePlayer(),
                gameState.getSecondaryOption(),
                true
            );
            this.subscribeToGameState();
            NewGameMessageBody body = new NewGameMessageBody(gameState);
            GameServerService.getInstance().broadcast(id, new Message(body, MessageType.NEW_GAME));
        }
    }

    private void initializeAi(){
        MarkerShape shape = null;
        int shapeIndex = new Random().nextInt(10);
        switch(shapeIndex){
            case 0: shape = MarkerShape.CAT; break;
            case 1: shape = MarkerShape.X; break;
            case 2: shape = MarkerShape.DRAGON; break;
            case 3: shape = MarkerShape.FILLED; break;
            case 4: shape = MarkerShape.FROWNY; break;
            case 5: shape = MarkerShape.O; break;
            case 6: shape = MarkerShape.OUTLINE; break;
            case 7: shape = MarkerShape.SMILEY; break;
            case 8: shape = MarkerShape.STAR; break;
        }

        Color color = null;
        int colorIndex = new Random().nextInt(10);
        switch(colorIndex){
            case 0: color = Color.YELLOW; break;
            case 1: color = Color.RED; break;
            case 2: color = Color.ORANGE; break;
            case 3: color = Color.PINK; break;
            case 4: color = Color.BLUE; break;
            case 5: color = Color.GREEN; break;
            case 6: color = Color.TEAL; break;
            case 7: color = Color.PURPLE; break;
            case 8: color = Color.BLACK; break;
        }

        String aiName = null;
        int nameIndex = new Random().nextInt(10);
        switch(nameIndex){
            case 0: aiName = "HAL 9000"; break;
            case 1: aiName = "T-800"; break;
            case 2: aiName = "GLaDOS"; break;
            case 3: aiName = "SHODAN"; break;
            case 4: aiName = "The Architect"; break;
            case 5: aiName = "Cortana"; break;
            case 6: aiName = "Data"; break;
            case 7: aiName = "TARS"; break;
            case 8: aiName = "R2D2"; break;
        }

        Ai ai = new Ai(color, aiName, shape);
        this.players = new Pair<Player, Player>(this.players.getValue0(), ai);
        gameState.setPlayerTwo(ai);
        gameState.setStatus(Status.IN_PROGRESS);
        System.out.println("Final status check: " + gameState.getStatus());
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
