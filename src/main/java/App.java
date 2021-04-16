import java.util.HashMap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

public class App extends HttpServlet {
    
    public static void main(String[] args) throws Exception {
        ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
        HashMap<UUID, Client> clients = new HashMap<UUID, Client>();
        Connector connector = null;
        ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
        Server server = null;

        final Integer PORT = Integer.parseInt(System.getenv("PORT"));
        System.out.println("PORT: " + PORT);

        try {
            server = new Server(PORT);
            ContextHandlerCollection handlers = new ContextHandlerCollection();
            ResourceHandler handler = new ResourceHandler();
            handlers.addHandler(createContextHandler("/", handler));

            Servlet websocketServlet = new JettyWebSocketServlet() {
                @Override 
                protected void configure(JettyWebSocketServletFactory factory) {
                    factory.addMapping("/", (req, res) -> new TestListener());
                }
            };
            
            ServletContextHandler servletContextHandler = new ServletContextHandler();
            servletContextHandler.addServlet(new ServletHolder(websocketServlet), "/ws");
            JettyWebSocketServletContainerInitializer.configure(servletContextHandler, null);
            handlers.addHandler(servletContextHandler);
        
            server.setHandler(handlers);
        
            server.start();
            server.join();

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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // try {
            //     serverSocket.close();            
            // }            
            // catch (Exception ex){}        
        }
    }
    
    private static ContextHandler createContextHandler(String contextPath, Handler wrappedHandler){
        ContextHandler ch = new ContextHandler (contextPath);
        ch.setHandler(wrappedHandler);
        ch.clearAliasChecks();
        ch.setAllowNullPathInfo(true);
        return ch;
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
