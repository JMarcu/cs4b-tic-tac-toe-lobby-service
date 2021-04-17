import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
import models.TestListener;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;
import models.ServerMessage.PlayerPropertiesMessageBody;
 
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class App {
    
    public static void main(String[] args) throws Exception {
        ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
        HashMap<UUID, Client> clients = new HashMap<UUID, Client>();
        ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

        final Integer PORT = Integer.parseInt(System.getenv("PORT"));
        System.out.println("PORT: " + PORT);
            
        // String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setHostname("0.0.0.0");
        tomcat.setPort(Integer.valueOf(webPort));

        // StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        // System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        // File additionWebInfClasses = new File("target/classes");
        // WebResourceRoot resources = new StandardRoot();
        // resources.addPreResources(
        //     new DirResourceSet(
        //         resources, 
        //         "/WEB-INF/classes",
        //         additionWebInfClasses.getAbsolutePath(),
        //         "/"
        //     )
        // );
        // ctx.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();

        System.out.println("Server started and listening for new connections...");

            // while(true){
            //     Lobby lobby = new Lobby(UUID.randomUUID().toString());
            //     lobbies.add(lobby);

            //     Socket clientSocket = serverSocket.accept();
            //     System.out.println("Client Socket Initialied :: isConnected " + clientSocket.isConnected());
            //     System.out.println("Client Socket Initialied :: getRemoteSocketAddress " + clientSocket.getRemoteSocketAddress());
                
            //     Client clientOne = new Client(clientSocket);
 
            //     clients.put(clientOne.getUuid(), clientOne);
            //     ClientHandler clientOneHandler = new ClientHandler(clientOne, lobby, clients);
            //     clientHandlers.add(clientOneHandler);
            //     System.out.println("Client One Connected: " + clientOne);
            //     PlayerData authPatchOne = new PlayerData(
            //         new SerializeableColor(Color.BLACK),
            //         "Player One",
            //         MarkerShape.X
            //     );
            //     PlayerPropertiesMessageBody authBodyOne = new PlayerPropertiesMessageBody(authPatchOne, clientOne.getUuid());
            //     clientOne.dispatchMessage(new Message(authBodyOne, MessageType.AUTHENTICATION_SUCCESS));
            //     // clientOne.dispatchMessage(new Message(llMsgBody, MessageType.LOBBY_LIST));

            //     Client clientTwo = new Client(serverSocket.accept());
            //     clients.put(clientTwo.getUuid(), clientTwo);
            //     ClientHandler clientTwoHandler = new ClientHandler(clientTwo, lobby, clients);
            //     clientHandlers.add(clientTwoHandler);
            //     System.out.println("Client Two Connected: " + clientTwo);
            //     PlayerData authPatchTwo = new PlayerData(
            //         new SerializeableColor(Color.BLACK),
            //         "Player Two",
            //         MarkerShape.O
            //     );
            //     PlayerPropertiesMessageBody authBodyTwo = new PlayerPropertiesMessageBody(authPatchTwo, clientTwo.getUuid());
            //     clientOne.dispatchMessage(new Message(authBodyTwo, MessageType.AUTHENTICATION_SUCCESS));

            //     //Searching for a random Lobby that has any open spots
            //     // int index = randomLobbySearch(lobbies);

            //     // System.out.println("Lobby index: " + index);

            //     // if(index != -1){
            //     //     System.out.println("preexisting lobby found");
            //     //     lobbies.get(index).addPlayer(player);
            //     //     System.out.println("Lobbysize: " + lobbies.get(index).getPlayers().size()); 
            //     //     System.out.println("added a player to a preexisting lobby");
            //     // }
            //     // else{

            //     //     lobbyCount++;
            //     //     HandleLobbies hl = new HandleLobbies(player, lobbyCount);
            //     //     //hl.getLobby().addPlayer(player);

            //     //     lobbies.add(hl.getLobby());

            //     //     System.out.println("added a new lobby");

            //     //     System.out.println();
            //     //     System.out.println();
            //     // }
            // }
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
