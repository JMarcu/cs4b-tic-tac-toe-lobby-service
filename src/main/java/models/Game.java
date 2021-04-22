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

    public Game(List<Integer> people){ // probably change what it takes in
        start = System.currentTimeMillis();
        end = -1;
        players = people;
        winner = -1;
        gameId = -1; // CHANGE //////////////////////////
        moves = new ArrayList<Pair<Integer,Integer>>();
    }

}
