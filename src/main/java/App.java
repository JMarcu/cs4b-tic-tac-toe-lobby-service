import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

import models.Client;
import models.ClientHandler;
import models.Lobby;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;

public class App {
    
    public static void main(String[] args) throws Exception {
        ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
        HashMap<UUID, Client> clients = new HashMap<UUID, Client>();
        ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
            
        // String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "4210";
        }
        tomcat.setPort(Integer.parseInt(webPort));
        System.out.println("Receiving webport value " + webPort);

        // Connector connector = new Connector(new Http11Nio2Protocol());
        // connector.setDomain("0.0.0.0");
        // connector.setPort(Integer.valueOf(webPort));
        // connector.
        // tomcat.setConnector(connector);

        // StandardContext ctx = (StandardContext) tomcat.addContext(
        //     "cs4b-tic-tac-toe-lobby-service.herokuapp.com", 
        //     contextPath, 
        //     contextName, 
        //     dir
        // )
        // Engine engine = new StandardEngine();
        // connector.getService().setContainer();
        // connector.getService().getContainer().setDefaultHost("cs4b-tic-tac-toe-lobby-service.herokuapp.com");
                
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

        String webappDirLocation = setupWebapp();
        // File thisJar = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

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

    private static String setupWebapp() throws Exception
    {
        File thisJar = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.out.println("thisJar: " + thisJar);
        String target = "tempwebapp";
        File targetFolder = new File(target + File.separator + "WEB-INF" + File.separator +"lib");
        System.out.println("targetFolder: " + targetFolder);
        targetFolder.mkdirs();
        copyFileUsingChannel(thisJar, new File(targetFolder.getPath() + File.separator + "lobby-service-1.0.jar"));
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = App.class.getResourceAsStream("index.html");
            if(stream == null) {
                throw new Exception("Cannot get resource \"index.html\" from Jar file.");
            }
 
            int readBytes;
            byte[] buffer = new byte[4096];
 
            resStreamOut = new FileOutputStream(target + File.separator + "index.html");
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
 
 
        }
        return target;
    }
 
    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        System.out.println(source);
        System.out.println(dest);
        FileInputStream sourceStream = null;
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceStream = (new FileInputStream(source));
            System.out.println("sourceStream: " + sourceStream);
            sourceChannel = sourceStream.getChannel();
            System.out.println("sourceChannel: " + sourceChannel);
            destChannel = new FileOutputStream(dest).getChannel();
            System.out.println("destChannel: " + destChannel);
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch(Exception e){
            e.printStackTrace();
        } finally{
        }
        sourceStream.close();
        destChannel.close();
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
