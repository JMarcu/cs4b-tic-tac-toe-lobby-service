package models;

import models.Player;
import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;
import java.lang.*;



public class Game {
    
    private long start;
    private long end;
    private List<Integer> players; // takes PlayerIds instead of Players, so Integer
    private List<Pair<Integer,Integer>> moves;
    private Integer winner;
    private Integer gameId;

    public Game(List<Integer> people, Integer gameNumber){ // probably change what it takes in
        start = System.currentTimeMillis();
        end = -1;
        players = people;
        winner = -1;
        gameId = gameNumber;
        moves = new ArrayList<Pair<Integer,Integer>>();
    }

    public void addPlayer(Integer player){
        players.add(player);
    }

    public void setEnd(){
        end = System.currentTimeMillis();
    }

    public void addMove(Pair<Integer,Integer> move){
        moves.add(move);
    }

    public void setWinner(Integer winnerId){
        winner = winnerId;
    }

    public Integer getWinner(){
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

    public List<Integer> getPlayers(){
        return players; // first player, second player, viewers
    }
}
