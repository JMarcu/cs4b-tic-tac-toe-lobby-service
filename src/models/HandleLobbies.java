package models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class HandleLobbies implements Runnable{

    private Socket playerOne;
    private ObjectInputStream player1In;
    private ObjectOutputStream player1Out;

    private Socket playerTwo;
    private ObjectInputStream player2In;
    private ObjectOutputStream player2Out;

    private Lobby lobby;
    
    public HandleLobbies(Socket playerOne)
    {
        this.playerOne = playerOne;

        lobby = new Lobby("");
    }

    public HandleLobbies(Socket playerOne, Socket playerTwo){

        System.out.println("Made it into HandleLobbies");

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        
        lobby = new Lobby("");
    }

    public void addPlayer(Socket playerTwo, Lobby myLobby){
        this.playerTwo = playerTwo;
        
        System.out.println("Made it into addPlayer");
        //add playerTwo to lobby
    }

    public int lobbySize(){
        return lobby.getPlayers().size();
    }

    @Override
    public void run(){     }  

    public Lobby getLobby(){ return this.lobby; }
}
// try {
      

//     Thread clientThread = new Thread(this);
// } catch (UnknownHostException e) {
//     e.printStackTrace();
// } catch (IOException e) {
//     e.printStackTrace();
// }