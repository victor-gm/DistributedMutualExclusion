package Network.V0;

import Model.Message;
import Model.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LWConnectionClient extends Thread
{
    private ObjectOutputStream OOS;
    private ObjectInputStream OIS;

    private Protocol p;
    private int id;


    public LWConnectionClient(Socket socket, Protocol p)
    {
        this.p = p;

        try {
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OIS = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                Message msg = (Message) OIS.readObject();
                //p.handleMsg(msg, id);
                if (!msg.getMessage().equals(Message.SET_ID)){
                    p.handleMsg(msg);
                }else{
                    this.id = msg.getSrcId();
                }
            } catch (IOException| ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message message)
    {
        try {
            OOS.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMyId() {
        return id;
    }
}
