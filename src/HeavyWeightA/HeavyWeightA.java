package HeavyWeightA;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HeavyWeightA extends Thread implements HeavyWeight {

    private ConnectionManager connection;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Token token;

    private String version = Config.getVersion();
    private int counter;
    private int id;
    private int group;


    public HeavyWeightA(){
        counter = 0;
        id = Config.HWA_ID;
        group = Config.LAMPORT_GROUP;
        token = new Token();
    }

    public void openHWPort(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            Socket socket = ss.accept();
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connection from HWB established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        Token t = null;
        try {
            t = (Token) ois.readObject();
            System.out.println("Token received with value: " + t.getTokenValue());
            if (t.getTokenValue()) {
                this.token.setTokenValue(t.getTokenValue());
                //notifyLWServer();
                connection.broadcast(new Message(0, Message.START,id,id));
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
        if (counter == Config.LAMPORT_NODES){
            System.out.println("ALL releases were received");
            System.out.println("Releasing TOKEN");
            //send message to B to release token.
            try {
                if (Config.getVersion().equals(Config.VERSION0)) oos.writeObject(new Token());
                else connection.sendRelease(new Message(Config.HWB_ID,Message.TOKEN));
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
        Scanner scan = new Scanner(System.in);
        System.out.println("Press enter when everyone is ready");
        String text = scan.nextLine(); //wait
        System.out.println("Sending a START to LWAs");
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

    }
}
