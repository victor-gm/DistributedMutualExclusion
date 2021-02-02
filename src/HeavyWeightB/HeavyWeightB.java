package HeavyWeightB;

import Model.Config;
import Model.HeavyWeight;
import Model.Message;
import Model.Token;
import Network.Interfaces.ConnectionManager;
import Network.V0.HWConnectionManager;
import Network.V1.HWRouterClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class HeavyWeightB extends Thread implements HeavyWeight {


    private ConnectionManager connection;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private String version = Config.getVersion();
    private int counter;
    private int id;
    private int group;
    private Token token;

    public HeavyWeightB(Token token){
        counter = 0;
        id = Config.HWB_ID;
        group = Config.RA_GROUP;
        this.token = token;
    }

    public void connectToHWA(int port)
    {
        try {
            Socket socket = new Socket(Config.LOCALHOST,port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connection to HWA established");
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Token t = null;
        try {
            t = (Token) ois.readObject();
            System.out.println("Token received with value: " + t.getTokenValue());
            if (t.getTokenValue()) {
                this.token.setTokenValue(t.getTokenValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void countRelease() {
        counter++;
        if (counter == Config.RA_NODES){
            System.out.println("ALL releases were received");
            System.out.println("Releasing TOKEN");
            //send message to B to release token.
            try {
                if (Config.getVersion().equals(Config.VERSION0)) oos.writeObject(new Token());
                else connection.sendRelease(new Message(Config.HWA_ID,Message.TOKEN));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startLWServer(int myPort) {
        if (version.equals(Config.VERSION0)) connection = new HWConnectionManager(myPort, this);
        else connection = new HWRouterClient(Config.Router,this);
        System.out.println("Starting LW Server");
        connection.connect();
    }

    @Override
    public void notifyLWServer() {
        System.out.println("Sending a START to LWBs");
        connection.broadcast(new Message(0, Message.START,id,id));
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public int getMyId() {
        return id;
    }

    @Override
    public void releaseToken() {
        this.token.setTokenValue(true);
    }
}
