package Lamport;

import Model.Message;
import Model.Config;
import Model.Protocol;
import Network.V0.Connection;
import Network.Interfaces.ConnectionManager;
import Network.V1.LWRouterClient;

import java.util.Scanner;


public class LamportMutex extends Thread implements Protocol
{
    private String version = Config.getVersion();
    private DirectClock v;
    private int [] q; // request queue

    private int N = Config.LAMPORT_NODES;
    private int myId;

    private ConnectionManager connection;
    private int ackCounter;

    public LamportMutex(int myId)
    {
        v = new DirectClock (N, myId);
        q = new int[N];
        for (int j = 0; j < N; j++) {
            q[j] = Integer.MAX_VALUE;
        }
        this.myId = myId;
    }


    public synchronized  void requestCS() {
        //Send ID so everyone knows who I am
        if (version.equals(Config.VERSION0)) broadcast(new Message(0,Message.SET_ID,0,getMyId()));

        ackCounter = N-1;
        start();
    }

    @Override
    public int getGroup() {
        return Config.LAMPORT_GROUP;
    }

    @Override
    public void run(){
        v.tick();
        q[myId] = v.getValue(myId);
        broadcast(new Message(v.getValue(myId), Message.REQUEST,myId,getMyId()));
        while (!okayCS()){
            while (ackCounter != 0){
                delay(); //para que el compilador no se vuelva loco en un bucle infinito vacío
            }
            delay(); //para que el compilador no se vuelva loco en un bucle infinito vacío
        }

        for (int i = 0; i < 10 ; i++){
            System.out.println( (i+1) + ". Soy el proceso A" + (myId+1));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        releaseCS();
        System.out.println("Thread dead");
    }

    private void delay(){
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private synchronized void releaseCS(){
        q[myId] = Integer.MAX_VALUE;
        Message msg = new Message(v.getValue(myId),Message.RELEASE, myId,getMyId());
        broadcast(msg);
        releaseHW(msg);
    }

    private void releaseHW(Message msg) {
        connection.sendRelease(msg);
    }

    boolean okayCS()
    {
        for (int j = 0; j < N; j++) {
            if (isGreater (q[myId], myId, q[j], j)){
                return false;
            }
            if (isGreater(q[myId], myId, v.getValue(j), j)){
                return false;
            }
        }
        return true;
    }

    boolean isGreater (int entry1, int pid1, int entry2, int pid2)
    {
        if(entry2 == Integer.MAX_VALUE) return false;
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    @Override
    public synchronized void handleMsg (Message m)
    {
        int timeStamp = m.getTimeStamp();
        v.receiveAction(m.getId(), timeStamp);
        if (m.getMessage().equals(Message.REQUEST))
        {
            q[m.getId()] = timeStamp;
            sendMessage(new Message( v.getValue(myId), Message.ACK,myId,m.getSrcId(),getMyId()));
        } else if (m.getMessage().equals(Message.RELEASE)){
            q[m.getId()] = Integer.MAX_VALUE;
        }else if(m.getMessage().equals(Message.ACK)){
            ackCounter--;
        }
    }


    private void sendMessage(Message m){
        connection.sendMessage(m);
    }

    private void broadcast(Message m){
        connection.broadcast(m);
    }

    public void startConnection(int HWport, int [] LWports, int myPort)
    {
        if (version.equals(Config.VERSION0)){
            connection = new Connection(HWport, LWports,myPort,this);
            connection.start();
            ready();
            connection.connect();
        }else{
            System.out.println("Router Version");
            connection = new LWRouterClient(HWport,this);
            connection.connect();
            connection.start();
        }
    }

    public void ready()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Press enter when everyone is ready");
        String text= scan.nextLine(); //wait
    }

    @Override
    public int getMyId(){
        return Config.LAMPORT_GROUP + myId;
    }

}
