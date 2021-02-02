package Network.V0;

import Model.HeavyWeight;
import Model.Message;
import Network.Interfaces.ConnectionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class HWConnectionManager extends ConnectionManager {

    private int myPort;
    private LinkedList<HWConnectionClient> clients;
    private HeavyWeight hw;

    public HWConnectionManager(int myPort, HeavyWeight hw){
        this.myPort = myPort;
        this.hw = hw;
        clients = new LinkedList<>();
    }


    @Override
    public void connect() {
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(myPort);
            while (true){
                Socket cs = ss.accept();
                HWConnectionClient hwc = new HWConnectionClient(cs, hw);
                hwc.start();
                clients.add(hwc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void broadcast(Message message) {
        for(HWConnectionClient cc: clients){
            cc.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void sendRelease(Message message) {

    }
}
