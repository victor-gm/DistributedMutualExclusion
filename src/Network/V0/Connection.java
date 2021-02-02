package Network.V0;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Model.*;
import Network.Interfaces.ConnectionManager;

import javax.swing.*;


public class Connection extends ConnectionManager
{
    private int HWport;
    private int[] LWports;
    private int myPort;

    private ObjectOutputStream HWoutput;
    private ObjectInputStream HWinput;

    private LinkedList<LWConnectionClient> clientList;
    private LinkedList<LWConnectionServer> serverList;

    private Protocol p;

    public Connection(int HWport, int[] LWports, int myPort, Protocol p)
    {
        this.HWport = HWport;
        this.LWports = LWports;
        this.myPort = myPort;

        clientList = new LinkedList<LWConnectionClient>();
        serverList = new LinkedList<LWConnectionServer>();

        this.p = p;
    }

    @Override
    public void run()
    {
        //Conexión con el HW
        try {
            System.out.println("connecting to HWA");
            Socket HWs = new Socket("localhost", HWport );
            HWoutput = new ObjectOutputStream(HWs.getOutputStream());
            HWinput = new ObjectInputStream(HWs.getInputStream());


            System.out.println("Starting server");
            ServerSocket LWs = new ServerSocket(myPort);

            for (int i = 0; i < LWports.length; i++)
            {
                Socket cs = LWs.accept();
                //LWConnectionClient lwConnectionClient = new LWConnectionClient(cs,p,i);
                LWConnectionClient lwConnectionClient = new LWConnectionClient(cs,p);
                lwConnectionClient.start();
                clientList.add(lwConnectionClient);
            }

            while(true)
            {
                System.out.println("in");
                //Esperamos el start del HW
                Message msg = null;
                try {
                    System.out.println("Waiting for HW");
                    msg = (Message) HWinput.readObject();
                    if(msg.getMessage().equals(Message.START)) p.requestCS();
                    else System.out.println("Message: " + msg.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Me conecto a toda la lista de puertos
    public void connect()
    {
        System.out.println("Connecting to servers");
        for (int i = 0; i < LWports.length; i++)
        {
            Socket cs = null;
            try {
                cs = new Socket("localhost",LWports[i]);
                //LWConnectionServer lwConnectionServer = new LWConnectionServer(cs, p, i);
                LWConnectionServer lwConnectionServer = new LWConnectionServer(cs, p);
                lwConnectionServer.start();
                serverList.add(lwConnectionServer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void broadcast(Message message)
    {
        for (LWConnectionServer lwConnectionServer: serverList)
        {
            lwConnectionServer.sendMessage(message);
        }
    }

    public void sendRelease(Message message){
        try {
            HWoutput.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message message)
    {
        for (LWConnectionClient lwConnectionClient: clientList)
        {
            if(lwConnectionClient.getMyId() == message.getDestId()){
                lwConnectionClient.sendMessage(message);
                break;
            }
        }
    }

}
