package interfaces;

import models.Player;
import models.Game;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
        String sql = "CREATE TABLE IF NOT EXISTS GameHistory (gameId INTEGER not NULL, playerOneId VARCHAR(255) not NULL, playerTwoId VARCHAR(255) not NULL, winner VARCHAR(255) not NULL, gameStart BIGINT not NULL, gameEnd BIGINT not NULL, moves VARCHAR(255) not NULL, spectators VARCHAR(255) not NULL, PRIMARY KEY (gameId))";
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

        try{
            Connection conn = getConn();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO GameHistory(gameId, playerOneId, playerTwoId, gameStatus, winner, gameStart, gameEnd, moves, spectators) VALUES (?,?,?,?,?,?,?,?)");
            stmt.setInt(1, game.getGameId()); // gameId
            stmt.setString(2, game.getPlayers().getKey().toString()); // playerOneId
            stmt.setString(3, game.getPlayers().getValue().toString()); // playerTwoId
            stmt.setString(4, game.getWinner().toString()); // winner
            stmt.setLong(5, game.getStart());//gameStart
            stmt.setLong(6, game.getEnd());//gameEnd
            stmt.setString(7, game.getMoves().toString()); //moves
            stmt.setString(8,game.getSpectators().toString()); //spectators
            stmt.executeUpdate();
            //close everything
            stmt.close();
            conn.close();
    
            }
            catch(Exception ex)
            {
                System.out.print("Error in GameDatabaseInterface");
            }

    }

    public List<Game> getGames(){
        List<Game> games;
        games = new ArrayList<Game>();
        //TODO
        try{

        Connection conn = getConn();
        String sql = "SELECT * FROM GameHistory";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);   
        Game game;
        while (rs.next())
        {
            int gameId = rs.getInt(1);
            UUID playerOne = UUID.fromString(rs.getString(2));
            UUID playerTwo = UUID.fromString(rs.getString(3));
            UUID winner = UUID.fromString(rs.getString(4));
            long gameStart = rs.getLong(5);
            long gameEnd = rs.getLong(6);
            List<Pair<Integer,Integer>> moves = new ArrayList<Pair<Integer,Integer>>();
            String prepMoves1 = rs.getString(7);
            prepMoves1.replace("[", "");
            prepMoves1.replace("]", "");
            String[] prepMoves = prepMoves1.split(", ");
            for (int i = 0; i < prepMoves.length; i++)
            {
                String[] prepMoves3 = prepMoves[i].split("=");
                moves.add(new Pair<Integer,Integer>(Integer.parseInt(prepMoves[0]), Integer.parseInt(prepMoves3[1])));
            }
            List<UUID> spectators = new ArrayList<UUID>(); 
            String prepSpectators = rs.getString(8);
            prepSpectators.replace("[", "");
            prepSpectators.replace("]", "");
            String[] prepSpectators1 = prepSpectators.split(", ");
            for (int i = 0; i < prepSpectators1.length; i++)
            {
                spectators.add(UUID.fromString(prepSpectators1[i]));
            }
            Pair<UUID,UUID> players = new Pair(playerOne,playerTwo);
            game = new Game(gameStart, gameEnd, spectators, moves, winner, gameId, players);
            games.add(game);
        }
        //close everything
        rs.close();
        stmt.close();
        conn.close();

        }
        catch(Exception ex)
        {
            System.out.print("Error in PlayerDatabaseInterface");
        }


        return games;
    }

    public Game getOneGame(Integer gameNum){

        //TODO
        Game game = new Game();
        try{

        Connection conn = getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GameHistory WHERE gameId = ?");
        stmt.setInt(1, gameNum);
        ResultSet rs = stmt.executeQuery();   
        
        while (rs.next())
        {
            int gameId = rs.getInt(1);
            UUID playerOne = UUID.fromString(rs.getString(2));
            UUID playerTwo = UUID.fromString(rs.getString(3));
            UUID winner = UUID.fromString(rs.getString(4));
            long gameStart = rs.getLong(5);
            long gameEnd = rs.getLong(6);
            List<Pair<Integer,Integer>> moves = new ArrayList<Pair<Integer,Integer>>();
            String prepMoves1 = rs.getString(7);
            prepMoves1.replace("[", "");
            prepMoves1.replace("]", "");
            String[] prepMoves = prepMoves1.split(", ");
            for (int i = 0; i < prepMoves.length; i++)
            {
                String[] prepMoves3 = prepMoves[i].split("=");
                moves.add(new Pair<Integer,Integer>(Integer.parseInt(prepMoves[0]), Integer.parseInt(prepMoves3[1])));
            }
            List<UUID> spectators = new ArrayList<UUID>(); 
            String prepSpectators = rs.getString(8);
            prepSpectators.replace("[", "");
            prepSpectators.replace("]", "");
            String[] prepSpectators1 = prepSpectators.split(", ");
            for (int i = 0; i < prepSpectators1.length; i++)
            {
                spectators.add(UUID.fromString(prepSpectators1[i]));
            }
            Pair<UUID,UUID> players = new Pair(playerOne,playerTwo);
            game = new Game(gameStart, gameEnd, spectators, moves, winner, gameId, players);
            game.setStart(gameStart);
            game.setEndManually(gameEnd);
            game.setAllSpectators(spectators);
            game.setAllMoves(moves);
            game.setWinner(winner);
            game.setGameId(gameId);
            game.setPlayers(players);
        }
        //close everything
        rs.close();
        stmt.close();
        conn.close();

        }
        catch(Exception ex)
        {
            System.out.print("Error in PlayerDatabaseInterface");
        }

        return game;
    }
}
