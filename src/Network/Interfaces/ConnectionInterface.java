package Network.Interfaces;

import Model.Message;

public interface ConnectionInterface {

    void broadcast(Message message);
    void sendMessage(Message message);
    void sendRelease(Message message);
    void connect();
}
