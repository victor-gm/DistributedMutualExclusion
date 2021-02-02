package Network.V0;

import Model.Message;
import Model.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LWConnectionServer extends Thread
{
    private ObjectOutputStream OOS;
    private ObjectInputStream OIS;

    private Protocol p;
    private int id;


    public LWConnectionServer(Socket socket,Protocol p)
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
                p.handleMsg(msg);
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
}
