package Network.V1;

import Model.Message;
import Model.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Router extends Thread{

    private int myPort;
    private ArrayList<RouterServer> clients;
    private static int [] delays = {100,200,300,400,250,500,1000,600};
    private static int delayCounter;

    public Router(){
        delayCounter = 0;
        myPort = Config.Router;
        clients = new ArrayList<>();
    }

    @Override
    public void run(){

        try {
            ServerSocket ss = new ServerSocket(myPort);
            while (true){
                Socket socket = ss.accept();
                RouterServer rs = new RouterServer(socket, this, delays[delayCounter]);
                rs.start();
                clients.add(rs);
                delayCounter++;
                if(delayCounter >= delays.length){
                    delayCounter = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcast(int group, Message msg){
        for (RouterServer c: clients){
            if (c.getGroup() == group && msg.getSrcId() != c.getMyId()){
                System.out.println("BROADCASTING TO.. " + c.getMyId() + " with group: " + c.getGroup());
                c.sendMessage(msg);
            }
        }
    }

    public synchronized void sendMessage(Message msg){
        for (RouterServer c: clients){
            if (c.getMyId() == msg.getDestId()){
                System.out.println("SENDING MSG to.. " + c.getMyId());
                c.sendMessage(msg);
            }
        }
    }
}
