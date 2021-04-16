package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lobby implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private String name;
    private ArrayList<UUID> players;
    private GameState.Status status;

    public Lobby(String name){
        this(UUID.randomUUID(), name, new ArrayList<UUID>(), GameState.Status.NEW);
    }

    public Lobby(UUID id, String name, ArrayList<UUID> players, GameState.Status status){
        this.id = id;
        this.name = name;
        this.players = players;
        this.status = status;
    }

    public void addPlayer(Player player){
        
        System.out.println("Made it into addPlayer - Lobby");

       // if(players.size() < 2){

            players.add(player.getUuid());
            System.out.println("Player was added to the lobby");
            System.out.println("Player UUID: " + player.getUuid());
            System.out.println("Lobby Name: " + this.name);
            System.out.println("Players.Size()" + players.size());
     //   }
        // else{
        //     System.out.println("Sorry but this lobby is full. Please find another match");
        // }
    }

    public void addPlayers(Player playerOne, Player playerTwo){
        
        System.out.println("Made it into addPlayers - Lobby");

       // if(players.size() < 2){

            players.add(playerOne.getUuid());
            players.add(playerOne.getUuid());
            System.out.println("Players were added to the lobby");
            System.out.println("PlayerOne UUID: " + playerOne.getUuid());
            System.out.println("PlayerTwo UUID: " + playerTwo.getUuid());
            System.out.println("Lobby Name: " + this.name);
            System.out.println("Players.Size()" + players.size());
     //   }
        // else{
        //     System.out.println("Sorry but this lobby is full. Please find another match");
        // }
    }
    
    public UUID getId(){ return this.id; }
    public String getName(){ return this.name; }
    public List<UUID> getPlayers(){ return Collections.unmodifiableList(this.players); }
    public GameState.Status getStatus(){ return this.status; }
}
