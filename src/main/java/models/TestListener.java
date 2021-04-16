package models;

import org.eclipse.jetty.websocket.api.WebSocketListener;

public class TestListener implements WebSocketListener {
    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len){
        System.out.println("Payload: " + payload);
    }

    /**
     * A WebSocket Text frame was received.
     *
     * @param message the message
     */
    @Override
    public void onWebSocketText(String message){
        System.out.println("message: " + message);
    }
}
