import java.util.Arrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import models.HandleClientConnection;
import models.HandleLobbies;
import models.Lobby;
import models.Player;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MoveMessageBody;

public class App {
    
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket;
        ArrayList<HandleClientConnection> clientConnectionList;
        ArrayList<Lobby> lobbyList;
        int lobbyCount = 0;

        serverSocket = null; 
        clientConnectionList = null;
        lobbyList = null;
        
        clientConnectionList = new ArrayList<HandleClientConnection>();
        lobbyList = new ArrayList<Lobby>();
        
        try {

            serverSocket = new ServerSocket(4210);

            System.out.println("Server started and listening for new connections...");

            while(true){
                Socket playerSocket = serverSocket.accept();
                System.out.println("playerOneSocket: " + playerSocket);

                HandleClientConnection clientConnection = new HandleClientConnection(playerSocket);
                clientConnectionList.add(clientConnection);
                System.out.println("clientConnection " + clientConnection);

                Player player = new Player();
                player.setSocket(playerSocket);
                System.out.println("Socket is placed inside of the player object");

                // Socket playerTwoSocket = serverSocket.accept();
                // totalClientConnections++;

                // HandleClientConnection clientConnection2 = new HandleClientConnection(playerTwoSocket);
                // clientConnectionList.add(clientConnection2);

                //HandleLobbies hl = new HandleLobbies(playerOneSocket, null);
                //System.out.println("hl" + hl);
                //lobbyList.add(hl.getLobby());
                

                Message llMsg = new Message(lobbyList, MessageType.LOBBY_LIST);
                System.out.println("llMsg" + llMsg);
                clientConnection.send(llMsg);

                //Searching for a random Lobby that has any open spots
                int index = randomLobbySearch(lobbyList);

                System.out.println("Lobby index: " + index);

                if(index != -1){
                    System.out.println("preexisting lobby found");
                    lobbyList.get(index).addPlayer(player);
                    System.out.println("Lobbysize: " + lobbyList.get(index).getPlayers().size()); 
                    System.out.println("added a player to a preexisting lobby");
                }
                else{

                    lobbyCount++;
                    HandleLobbies hl = new HandleLobbies(player, lobbyCount);
                    //hl.getLobby().addPlayer(player);

                    lobbyList.add(hl.getLobby());

                    System.out.println("added a new lobby");

                    System.out.println();
                    System.out.println();
                }

                System.out.println();
                System.out.println();

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
