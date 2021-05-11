package interfaces;

import models.Player;

public class PlayerDatabaseInterface {

    public Boolean validatePassword(String playerId, String password){
        Boolean isValid = true; // make it not 0 later
        //TODO
        return isValid;
    }
    
    public Boolean setPassword(String playerId, String password){
        Boolean isSet = true; // change
        //TODO
        //use sha1 or other hash generator to make hash
        //only allow to set password when making account
        //INSERT INTO player(playerNum, passwordHash)
        //Values(playerId, hash);
        return isSet;
    }

    public String getToken(String playerId){
        //TODO
        //SELECT token FROM tokens WHERE playerNum = playerId;
        return "TODO";
    }

    public void setToken(String playerID, String token){
        //TODO
        //INSERT INTO RefreshTokens
        //throw an error if it doesn't work
    }

    public void deleteToken(String playerId){
        //TODO
    }

    public Player getPlayer(String playerId){
        Player player;
        player = new Player(); // CHANGE
        //TODO
        return player;
    }

    public void setPlayer(Player player){
        //TODO
    }
}
