package Network.V1;

import Model.Config;
import Model.HeavyWeight;
import Model.Message;
import Model.Protocol;
import Network.Interfaces.ConnectionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HWRouterClient extends ConnectionManager {

    private int routerPort;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private HeavyWeight hw;

    public HWRouterClient(int routerPort, HeavyWeight hw){
        this.routerPort = routerPort;
        this.hw = hw;
    }

    public void connect(){
        Socket socket = null;
        try {
            socket = new Socket(Config.LOCALHOST, routerPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            //send ID and group to the router
            sendMessage(new Message(0,Message.SET_ID,hw.getMyId(),hw.getMyId()));
            sendMessage(new Message(0,Message.SET_GROUP,hw.getGroup(),hw.getMyId()));

            start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (true){
            try {
                Message msg = (Message) ois.readObject();
                if(msg.getMessage().equals(Message.RELEASE)){
                    hw.countRelease();
                }else if(msg.getMessage().equals("TOKEN")){
                    hw.releaseToken();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void broadcast(Message message) {
        sendMessage(new Message(0,Message.BROADCAST,0,0));
        sendMessage(message);
    }

    public void sendMessage(Message msg){
        try {
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRelease(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
