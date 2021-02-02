package Network.V1;

import Model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RouterServer extends Thread{

    private Router router;
    private int id;
    private int group;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int delay;

    public RouterServer(Socket socket, Router router, int delay){
        this.router = router;
        this.delay = delay;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (true){
            try {
                Message msg = (Message) ois.readObject();
                //System.out.println("ROUTER SERVER msg received: " + msg.getMessage());
                switch (msg.getMessage()){
                    case Message.REQUEST:
                    case Message.OKAY:
                    case Message.ACK:
                    case Message.RELEASE:
                    case Message.START:
                    case Message.TOKEN:
                        router.sendMessage(msg);
                        break;
                    case Message.BROADCAST:
                        msg = (Message) ois.readObject();
                        router.broadcast(group,msg);
                        break;
                    case Message.SET_ID:
                        this.id = msg.getId();
                        break;
                    case Message.SET_GROUP:
                        this.group = msg.getId();
                        break;
                    default:
                        System.out.println("Message received: " + msg.getMessage());
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendMessage(Message msg){

        try {
            sleep(delay); //wait the delay
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getGroup() {
        return group;
    }

    public int getMyId() {
        return id;
    }

    public int getDelay(){
        return delay;
    }
}
