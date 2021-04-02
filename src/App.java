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
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.MoveMessageBody;

public class App {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket;
        ArrayList<HandleClientConnection> clientConnectionList;
        ArrayList<Lobby> lobbyList;
        int totalClientConnections;

        serverSocket = null; 
        // gameState = null;
        clientConnectionList = null;
        lobbyList = null;
        // messages = null;
        totalClientConnections = 0;
        
        clientConnectionList = new ArrayList<HandleClientConnection>();
        lobbyList = new ArrayList<Lobby>();

        try {

            serverSocket = new ServerSocket(4210);

            System.out.println("Server started and listening for new connections...");

            while(true){
                Socket playerOneSocket = serverSocket.accept();
                System.out.println("playerOneSocket: " + playerOneSocket);
                totalClientConnections++;

                HandleClientConnection clientConnection1 = new HandleClientConnection(playerOneSocket);
                clientConnectionList.add(clientConnection1);
                System.out.println("clientConnection1" + clientConnection1);

                // Socket playerTwoSocket = serverSocket.accept();
                // totalClientConnections++;

                // HandleClientConnection clientConnection2 = new HandleClientConnection(playerTwoSocket);
                // clientConnectionList.add(clientConnection2);
                HandleLobbies hl = new HandleLobbies(playerOneSocket, null);
                System.out.println("hl" + hl);
                lobbyList.add(hl.getLobby());
                System.out.println("added the lobby");

                Message llMsg = new Message(lobbyList, MessageType.LOBBY_LIST);
                System.out.println("llMsg" + llMsg);
                clientConnection1.send(llMsg);
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
        
    // public void checkDeadLobbies(){
    //     for(int i = 0; i < lobbyList.size(); i++){
    //         if(lobbyList.get(i) != null && lobbyList.get(i).getPlayers().size() == 0){
    //             System.out.println("Removing Lobby with id of: " + lobbyList.get(i).getId());
    //             lobbyList.remove(i);
    //         }
    //     }
    // }
}
