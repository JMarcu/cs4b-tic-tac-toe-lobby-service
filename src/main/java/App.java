import java.util.HashMap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import javafx.scene.paint.Color;
import models.Client;
import models.ClientHandler;
import models.Lobby;
import models.MarkerShape;
import models.Player;
import models.PlayerData;
import models.SerializeableColor;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.PlayerPropertiesMessageBody;

public class App {
    
    public static void main(String[] args) throws Exception {
        ArrayList<ClientHandler> clientHandlers;
        HashMap<UUID, Client> clients;
        ArrayList<Lobby> lobbies;
        ServerSocket serverSocket;

        clientHandlers = new ArrayList<ClientHandler>();
        clients = new HashMap<UUID, Client>();
        lobbies = new ArrayList<Lobby>();
        serverSocket = null; 
        
        try {
            serverSocket = new ServerSocket(4210);

            System.out.println("Server started and listening for new connections...");

            while(true){
                Lobby lobby = new Lobby(UUID.randomUUID().toString());
                lobbies.add(lobby);

                Client clientOne = new Client(serverSocket.accept());
                clients.put(clientOne.getUuid(), clientOne);
                ClientHandler clientOneHandler = new ClientHandler(clientOne, lobby, clients);
                clientHandlers.add(clientOneHandler);
                System.out.println("Client One Connected: " + clientOne);
                PlayerData authPatchOne = new PlayerData(
                    new SerializeableColor(Color.BLACK),
                    "Player One",
                    MarkerShape.X
                );
                PlayerPropertiesMessageBody authBodyOne = new PlayerPropertiesMessageBody(authPatchOne, clientOne.getUuid());
                clientOne.dispatchMessage(new Message(authBodyOne, MessageType.AUTHENTICATION_SUCCESS));
                // clientOne.dispatchMessage(new Message(llMsgBody, MessageType.LOBBY_LIST));

                Client clientTwo = new Client(serverSocket.accept());
                clients.put(clientTwo.getUuid(), clientTwo);
                ClientHandler clientTwoHandler = new ClientHandler(clientTwo, lobby, clients);
                clientHandlers.add(clientTwoHandler);
                System.out.println("Client Two Connected: " + clientTwo);
                PlayerData authPatchTwo = new PlayerData(
                    new SerializeableColor(Color.BLACK),
                    "Player Two",
                    MarkerShape.O
                );
                PlayerPropertiesMessageBody authBodyTwo = new PlayerPropertiesMessageBody(authPatchTwo, clientTwo.getUuid());
                clientOne.dispatchMessage(new Message(authBodyTwo, MessageType.AUTHENTICATION_SUCCESS));

                //Searching for a random Lobby that has any open spots
                // int index = randomLobbySearch(lobbies);

                // System.out.println("Lobby index: " + index);

                // if(index != -1){
                //     System.out.println("preexisting lobby found");
                //     lobbies.get(index).addPlayer(player);
                //     System.out.println("Lobbysize: " + lobbies.get(index).getPlayers().size()); 
                //     System.out.println("added a player to a preexisting lobby");
                // }
                // else{

                //     lobbyCount++;
                //     HandleLobbies hl = new HandleLobbies(player, lobbyCount);
                //     //hl.getLobby().addPlayer(player);

                //     lobbies.add(hl.getLobby());

                //     System.out.println("added a new lobby");

                //     System.out.println();
                //     System.out.println();
                // }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();            
            }            
            catch (Exception ex){}        
        }
    }
        
    public static int randomLobbySearch(ArrayList<Lobby> lobbyList){
        
        for(int i = 0; i < lobbyList.size(); i++){
            if(lobbyList.get(i).getPlayers().size() < 2){

                System.out.println("randomLobbySearch: Found an OPEN lobby");
                return i;
            }
        }

        System.out.println("randomLobbySearch: NO open lobbies");
        return -1;
    }

    public void checkDeadLobbies(ArrayList<Lobby> lobbyList){
        for(int i = 0; i < lobbyList.size(); i++){
            if(lobbyList.get(i).getPlayers().size() < 2){
                lobbyList.remove(i);
            }
        }
    }
}
