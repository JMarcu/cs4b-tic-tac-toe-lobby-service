package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import interfaces.Sender;
import models.GameServer;
import models.Lobby;
import models.Player;
import models.ServerMessage.Message;

public class GameServerService {
    private static GameServerService instance;
    
    private HashMap<UUID, Sender> clients;
    private HashMap<UUID, GameServer> lobbyMap;

    private GameServerService(){
        this.clients = new HashMap<UUID, Sender>();
        this.lobbyMap = new HashMap<UUID, GameServer>();
    }

    public GameServer getGameServer(UUID id){
        return lobbyMap.get(id);
    }

    public ArrayList<GameServer> listGameServers(){
        return new ArrayList<GameServer>(lobbyMap.values());
    }

    public ArrayList<Lobby> listAsLobbies(){
        final ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
        lobbyMap.values().forEach((GameServer gameServer) -> {
            lobbies.add(gameServer.toLobby());
        });
        return lobbies;
    }

    public void addGameServer(GameServer server){
        this.lobbyMap.put(server.getId(), server);
    }

    public boolean addPlayer(UUID lobbyId, Player player, Sender sender){
        boolean success = this.lobbyMap.get(lobbyId).addPlayer(player);
        if(success){
            this.clients.put(player.getUuid(), sender);
        }
        return success;
    }

    public boolean removePlayer(Player player, Sender sender){
        final ArrayList<Lobby> lobbies = new ArrayList<Lobby>(lobbyMap.values());
        lobbies.forEach((Lobby lobby) -> {
            if(lobby.hasPlayer(player.getUuid())){
                removePlayer(lobby.getId(), player, sender);
            }
        });
        return lobbies.size() > 0;
    }

    public boolean removePlayer(UUID lobbyId, Player player, Sender sender){
        boolean success =  this.lobbyMap.get(lobbyId).removePlayer(player);
        if(success){
            this.clients.put(player.getUuid(), sender);
        }
        return success;
    }

    public void broadcast(UUID lobbyId, Message message){
        this.broadcast(lobbyId, message, null);
    }

    public void broadcast(UUID lobbyId, Message message, UUID exclude){
        Set<UUID> clientKeys = this.clients.keySet();
        clientKeys.remove(exclude);
        clientKeys.forEach((UUID playerId) -> {
            Sender client = this.clients.get(playerId);
            try {
                client.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static GameServerService getInstance(){
        if(instance == null){
            instance = new GameServerService();
        }

        return instance;
    }
}
