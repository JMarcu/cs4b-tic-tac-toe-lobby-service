package models;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import models.ServerMessage.Message;

public class ClientHandler implements Runnable{
    private Client client;
    private HashMap<UUID, Client> clientMap;
    private LinkedBlockingQueue<Message> msgQueue;
    private Lobby lobby;
    private MessageHandler messageHandler;
    
    public ClientHandler(Client client, Lobby lobby, HashMap<UUID, Client> clientMap){
        this.client = client;
        this.clientMap = clientMap;
        this.lobby = lobby;
        this.msgQueue = new LinkedBlockingQueue<Message>();
        
        this.messageHandler = new MessageHandler(client, clientMap, lobby, msgQueue);
        
        Thread clientThread = new Thread(this);
        clientThread.start();

        Thread msgHandlerThread = new Thread(messageHandler);
        msgHandlerThread.start();
    }

    @Override
    public void run(){
        try {
            while(true){
                Object msg = client.readObject();
                
                if(!(msg instanceof Message)){
                    throw new Exception("Unknown message format.");
                } else{
                    this.msgQueue.add((Message)msg);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
