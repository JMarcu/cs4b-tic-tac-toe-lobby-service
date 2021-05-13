package models;

import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;

public class Game {
    
    private long start;
    private long end;
    private List<String> spectators; // takes PlayerIds instead of Players, so Integer
    private List<Pair<Integer,Integer>> moves;
    private String winner;
    private Integer gameId;
    private Pair<String,String> players;

    public Game(Pair<String,String> people, Integer gameNumber){ // probably change what it takes in
        start = System.currentTimeMillis();
        end = -1;
        players = people;
        winner = "";
        gameId = gameNumber;
        moves = new ArrayList<Pair<Integer,Integer>>();
        spectators = new ArrayList<String>();
    }

    public void addSpecator(String spectator){
        spectators.add(spectator);
    }

    public void setEnd(){
        end = System.currentTimeMillis();
    }

    public void addMove(Pair<Integer,Integer> move){
        moves.add(move);
    }

    public void setWinner(String winnerId){
        winner = winnerId;
    }

    public String getWinner(){
        return winner;
    }

    public Integer getGameId(){
        return gameId;
    }

    public long getStart(){
        return start;
    }

    public long getEnd(){
        return end;
    }

    public List<Pair<Integer,Integer>> getMoves(){
        return moves;
    }

    public Pair<String,String> getPlayers(){
        return players; // first player, second player
    }

    public List<String> getSpectators(){
        return spectators;
    }
}
