package models.ServerMessage;

public enum MessageType {
    AUTHENTICATION_REQUEST, // Request from the client to authenticate.
    AUTHENTICATION_RESULT, 
    AUTHENTICATION_ACKNOWLEDGED, // Final step in handshake, client is ready for messages.
    CHAT, 
    CONNECTION,
    CREATE_LOBBY,
    LOBBY_LIST,
    MOVE,
    PLAYER_PROPERTIES,
    REQUEST_PLAYER,
}
