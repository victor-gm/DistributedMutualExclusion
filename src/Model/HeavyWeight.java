package Model;

public interface HeavyWeight {

    void countRelease();
    void startLWServer(int myPort);
    void notifyLWServer();
    int getGroup();
    int getMyId();
    void releaseToken();
}
