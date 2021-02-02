package Network.V1;

import Model.Message;
import Model.Config;
import Model.Protocol;
import Network.Interfaces.ConnectionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LWRouterClient extends ConnectionManager {

    private int HWPort;
    private Protocol p;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public LWRouterClient(int HWPort, Protocol p){
        this.HWPort = HWPort;
        this.p = p;
    }

    public void connect(){
        Socket socket = null;
        try {
            socket = new Socket(Config.LOCALHOST, HWPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            if (Config.getVersion().equals(Config.VERSION1)){
                sendMessage(new Message(0,Message.SET_ID,p.getMyId(),p.getMyId()));
                sendMessage(new Message(0,Message.SET_GROUP,p.getGroup(),p.getMyId()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (true){
            try {
                Message msg = (Message) ois.readObject();
                if(!msg.getMessage().equals(Message.START)){
                    p.handleMsg(msg);
                }else{
                    p.requestCS();
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
