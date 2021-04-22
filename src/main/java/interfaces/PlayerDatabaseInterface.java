package interfaces;

import models.Player;

public class PlayerDatabaseInterface {

    public Integer validatePassword(String playerId, String password){
        Integer hash = 0; // make it not 0 later
        //TODO
        return hash;
    }
    
    public Boolean setPassword(String playerId, String password){
        Boolean isSet = true; // change
        //TODO
        return isSet;
    }

    public String getToken(String playerId){
        //TODO
        return "TODO";
    }

    public void setToken(String playerID, String token){
        //TODO
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
