package Model;

public interface Protocol
{
    //void handleMsg(Message m, int id);
    void handleMsg(Message m);
    int getMyId();
    void requestCS();
    int getGroup();
}
