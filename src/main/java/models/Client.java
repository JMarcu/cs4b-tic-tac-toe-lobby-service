package models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.paint.Color;
import models.ServerMessage.Message;

public class Client extends Player{
    private static final long serialVersionUID = 1L;

    /** The socket that is tied to the player. */
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(Socket socket) {
        this(Color.BLACK, UUID.randomUUID(), "Player", MarkerShape.X, socket);
    }

    public Client(Color color, UUID id, String name, MarkerShape shape, Socket socket){
        super(color, id, name, shape);
        this.socket = socket;
        this.out = null;
        this.in = null;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the socket of the player.
     * @return The the socket of the player.
     */
    public Socket getSocket() { return this.socket; }

    public Object readObject() throws ClassNotFoundException, IOException {
        return in.readObject();
    }

    public void dispatchMessage(Message msg){
        try {
            System.out.println("Dispatching Message of Type: " + msg.getType());
            this.out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
