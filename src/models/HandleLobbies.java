package models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import models.ServerMessage.*;

public class HandleLobbies implements Runnable{

    private Player playerOne;
    private Socket playerOneSocket;
    private ObjectInputStream player1In;
    private ObjectOutputStream player1Out;

    private Player playerTwo;
    private Socket playerTwoSocket;
    private ObjectInputStream player2In;
    private ObjectOutputStream player2Out;

    private Lobby lobby;
    
    public HandleLobbies(Player playerOne, int count)
    {
        System.out.println("Made it into HandleLobbies");
        this.playerOne = playerOne;

        lobby = new Lobby("Lobby " + count);

        lobby.addPlayer(playerOne);
    }

    public HandleLobbies(Player playerOne, Player playerTwo, int count){

        try {
        System.out.println("Made it into HandleLobbies");

        this.playerOne = playerOne;
        playerOneSocket = this.playerOne.getSocket();
        player1In = new ObjectInputStream(playerOneSocket.getInputStream());
        player1Out = new ObjectOutputStream(playerOneSocket.getOutputStream());

        this.playerTwo = playerTwo;
        playerTwoSocket = this.playerTwo.getSocket();
        player2In = new ObjectInputStream(playerTwoSocket.getInputStream());
        player2Out = new ObjectOutputStream(playerTwoSocket.getOutputStream());

        lobby = new Lobby("Lobby " + count);

        lobby.addPlayers(playerOne, playerTwo);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPlayer(Player player){
        // this.playerTwo = playerTwo;
        
        // System.out.println("Made it into addPlayer");
        // //add playerTwo to lobby

        // lobby.addPlayer();

        System.out.println("Made it into addPlayer - HandleLobbies");

        if(lobby.getPlayers().size() == 0){
            playerOneSocket = this.playerOne.getSocket();
            lobby.addPlayer(player);
        }
        
        if(lobby.getPlayers().size() == 1){
            playerTwoSocket = this.playerTwo.getSocket();
            lobby.addPlayer(player); 
        }
    }

    public int lobbySize(){
        return lobby.getPlayers().size();
    }

    @Override
    public void run(){     }  

    public Lobby getLobby(){ return this.lobby; }

    private void onMessage(Message message){
        switch(message.getType()){
            case CHAT: 
                break;
            case CONNECTION:
                break;
            case LOBBY_LIST:
                break;
            case MOVE:
                break;
            case PLAYER_PROPERTIES:
                break;
            case REQUEST_PLAYER:
                break;
            default:
                System.err.println("Unknown message type received from server :: " + message.getType());
                break;
        }
    }
}
// try {
      

//     Thread clientThread = new Thread(this);
// } catch (UnknownHostException e) {
//     e.printStackTrace();
// } catch (IOException e) {
//     e.printStackTrace();
// }