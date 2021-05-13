package interfaces;

import java.io.IOException;
import javax.websocket.Session;
import models.ServerMessage.Message;

public interface Sender {
    public void close(Session session);
    public void send(Message message) throws IOException;
}
