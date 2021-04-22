package interfaces;

import models.Player;
import models.Game;
import java.util.List;
import java.util.ArrayList;

import javafx.util.Pair;

public class GameDatabaseInterface {
    void addGame (List<Integer> players, long start, long end, List<Pair<Integer,Integer>>moves, Integer winner, Integer id){
        //TODO
        //might need to change types what is coming in
    }

    List<Game> getGames(){
        List<Game> games;
        games = new ArrayList<Game>();
        //TODO
        return games;
    }
}
