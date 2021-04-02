package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import models.ServerMessage.Message;

public class HandleClientConnection implements Runnable{
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public HandleClientConnection(Socket client){
        this.client = client;
        try {
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("out" + out);
        Thread clientThread = new Thread(this);
        clientThread.start();
    }

    @Override
    public void run(){
        try {
            System.out.println("client" + client);
            in = new ObjectInputStream(client.getInputStream());
            while(true){
                Object msg = in.readObject();
                System.out.println("received message");
                System.out.println(msg);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(Message message){
        try {
            this.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
