package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import models.ServerMessage.ConnectionMessageBody;
import models.ServerMessage.Message;
import models.ServerMessage.MessageType;

public class MessageHandler implements Runnable {
    Lobby lobby;
    LinkedBlockingQueue<Message> msgQueue;
    HashMap<UUID, Client> clientMap;
    Client client;

    final int POLL_TIME = 500;

    MessageHandler(
        Client client,
        HashMap<UUID, Client> clientMap,
        Lobby lobby,
        LinkedBlockingQueue<Message> msgQueue
    ){
        this.client = client;
        this.clientMap = clientMap;
        this.lobby = lobby;
        this.msgQueue = msgQueue;
    }
    
    public void handleAuthenticationRequest(){
        // boolean validToken = request.getToken() != "";
        
        // AuthenticationResultMessageBody response = new AuthenticationResultMessageBody(validToken);
        // Message message = new Message(response, MessageType.AUTHENTICATION_RESULT);
        // try {
        //     sender.send(message);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    @Override
    public void run() {
        while(true){
            try {
                final Message msg = msgQueue.poll(POLL_TIME, TimeUnit.MILLISECONDS);
                if(msg != null){
                    System.out.println("Message Received: " + msg);
                    // switch(msg.getType()){
                    //     case AUTHENTICATION_ACKNOWLEDGED:
                    //         System.out.println("Type: AUTHENTICATION_ACKNOWLEDGED");

                    //         //TODO Implement the lobby list but for real.
                    //         // The MessageHandler should be able to subscribe to the list of lobbies from App.java
                    //         ArrayList<Lobby> llMsgBody = new ArrayList<Lobby>();
                    //         llMsgBody.add(lobby);
                    //         client.dispatchMessage(new Message(llMsgBody, MessageType.LOBBY_LIST));
                    //         break;
                    //     case AUTHENTICATION_REQUEST:
                    //         break;
                    //     case AUTHENTICATION_SUCCESS:
                    //         break;
                    //     case CHAT:
                    //         break;
                    //     case CONNECTION:
                    //         System.out.println("Type: CONNECTION");
                    //         ConnectionMessageBody body = (ConnectionMessageBody) msg.getBody();
                    //         System.out.println("Player ID: " + body.getPlayerId());
                    //         if(clientMap.containsKey(body.getPlayerId())){
                    //             System.out.println("Contains Key");
                    //             if(body.getType() == ConnectionMessageBody.Type.JOIN){
                    //                 System.out.println("Join Message...");
                    //                 lobby.addPlayer(clientMap.get(body.getPlayerId()));
                    //                 System.out.println("Player Added Successfully");
                    //                 lobby.getPlayers().forEach((UUID playerId) -> {
                    //                     System.out.println("For Player " + playerId);
                    //                     if(clientMap.containsKey(playerId)){
                    //                         System.out.println("Contains Key");
                    //                         Client client = clientMap.get(playerId);
                    //                         client.dispatchMessage(msg);
                    //                     }
                    //                 });
                    //             } else{
                    //                 System.out.println("Leave Message");
                    //             }
                    //         }
                    //         break;
                    //     case CREATE_LOBBY:
                    //         break;
                    //     case LOBBY_LIST:
                    //         break;
                    //     case MOVE:
                    //         break;
                    //     case PLAYER_PROPERTIES:
                    //         break;
                    //     case REQUEST_PLAYER:
                    //         System.out.println("Type: REQUEST_PLAYER");
                    //         UUID playerId = (UUID) msg.getBody();
                    //         if(clientMap.containsKey(playerId)){
                    //             Client client = clientMap.get(playerId);
                    //             PlayerData playerDefinition = new PlayerData(
                    //                 new SerializeableColor(client.getColor()),
                    //                 client.getName(),
                    //                 client.getShape()
                    //             );
                    //             PlayerPropertiesMessageBody playerDefinitionBody = new PlayerPropertiesMessageBody(playerDefinition, client.getUuid());
                    //             client.dispatchMessage(new Message(playerDefinitionBody, MessageType.PLAYER_PROPERTIES));
                    //         }
                    //         break;
                    //     default:
                    //         break;
                    // }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
