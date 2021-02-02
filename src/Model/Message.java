package Model;

import java.io.Serializable;

public class Message implements Serializable
{
    public static final String START = "START";
    public static final String REQUEST = "REQUEST";
    public static final String ACK = "ACK";
    public static final String RELEASE   = "RELEASE";
    public static final String OKAY = "OKAY";

    public static final String SET_ID = "SETID";
    public static final String SET_GROUP = "SET_GROUP";
    public static final String BROADCAST = "BROADCAST";
    public static final String TOKEN = "TOKEN";

    private int timeStamp;
    private String message;
    private int id;
    private int destId;
    private int srcId;

    public Message(int destId, String message){
        this.destId = destId;
        this.message = message;
    }

    public Message(int timeStamp, String message, int id, int srcId) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.srcId = srcId;
    }

    public Message(int timeStamp, String message, int id, int destId, int srcId) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.destId = destId;
        this.srcId = srcId;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDestId() {
        return destId;
    }

    public int getSrcId() {
        return srcId;
    }
}
