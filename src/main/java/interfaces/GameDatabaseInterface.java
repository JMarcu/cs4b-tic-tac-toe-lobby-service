package interfaces;

import models.Player;
import models.Game;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.UUID;

import javafx.util.Pair;

import org.postgresql.Driver;

import javafx.scene.paint.Color;

import java.net.URI;
import java.net.URISyntaxException;
import models.MarkerShape;
import models.SerializeableColor;


public class GameDatabaseInterface {

    private static GameDatabaseInterface instance = null; // singleton pattern

    private GameDatabaseInterface()
    {
        // constructor code
        try{
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        //create Game History table -------------- MOVE
        String sql = "CREATE TABLE IF NOT EXISTS GameHistory (gameId INTEGER not NULL, playerOneId VARCHAR(255) not NULL, playerTwoId VARCHAR(255) not NULL, gameStatus INTEGER not NULL, winner VARCHAR(255) not NULL, gameStart BIGINT not NULL, gameEnd BIGINT not NULL, moves VARCHAR(255) not NULL, spectators VARCHAR(255) not NULL, PRIMARY KEY (gameId))";
        stmt.executeUpdate(sql);

        //close everything
        stmt.close();
        conn.close();

        }
        catch(Exception ex)
        {
            System.out.print("Error in GameDatabaseInterface");
        }
    }

    public static GameDatabaseInterface getInstance()
    {
        if (instance == null)
            instance = new GameDatabaseInterface();
        return instance;
    }

    private Connection getConn() throws URISyntaxException, SQLException
    {
        String dbUrl = System.getenv("HEROKU_POSTGRESQL_CYAN_URL");
        return DriverManager.getConnection(dbUrl);
    }


    public void addGame (Game game){
        //TODO
        //might need to change types what is coming in

        

    }

    public List<Game> getGames(){
        List<Game> games;
        games = new ArrayList<Game>();
        //TODO
        return games;
    }
}
