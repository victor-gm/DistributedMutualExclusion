package Network.V0;

import Model.HeavyWeight;
import Model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HWConnectionClient extends Thread{

    private ObjectOutputStream OOS;
    private ObjectInputStream OIS;

    HeavyWeight hw;

    public HWConnectionClient(Socket socket, HeavyWeight hw) {
        this.hw = hw;
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
                if (msg.getMessage().equals(Message.RELEASE)){
                    hw.countRelease();
                }else{
                    System.out.println("HWClient msg: " + msg.getMessage());
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

}
