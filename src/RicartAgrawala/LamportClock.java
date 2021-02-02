package RicartAgrawala;

public class LamportClock {

    int c;
    public LamportClock () {
        c = 1;
    }

    public int getValue () {
        return c;
    }

    // on internal events
    public void tick () {
        c = c + 1;
    }

    public void sendAction() {
        // include c in message
        c = c + 1;
    }

    public void receiveAction (int src, int sentValue) {
        c = Math.max(c, sentValue) + 1;
    }

}
