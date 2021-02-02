package RicartAgrawala;

import Model.Config;
import Model.Message;
import Model.Protocol;
import Network.V0.Connection;
import Network.Interfaces.ConnectionManager;
import Network.V1.LWRouterClient;

import java.util.LinkedList;
import java.util.Scanner;

public class RAMutex extends Thread implements Protocol {

    private String version = Config.getVersion();
    private static final int NUM_PROCESSES = Config.RA_NODES;

    private int myts;
    private LamportClock c = new LamportClock();
    private LinkedList<Integer> pendingQ = new LinkedList<Integer>();
    private int numOkay;
    private int myId;

    private ConnectionManager connection;

    public RAMutex(int myId){
        this.myId = myId;
        myts = Integer.MAX_VALUE;
        numOkay = 0;
    }

    public synchronized void requestCS() {
        if (version.equals(Config.VERSION0)) broadcast(new Message(0, Message.SET_ID, 0, getMyId()));

        start();
    }

    @Override
    public void run(){
        c.tick();
        myts = c.getValue();
        broadcast(new Message(myts,Message.REQUEST, myId, getMyId())); //mandarle un mensaje a todos
        numOkay = 0;
        while (numOkay < NUM_PROCESSES - 1){ //esperar los OKAYs
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 10 ; i++){
            System.out.println( (i+1) + ". Soy el proceso B" + (myId+1));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        releaseCS();
    }

    @Override
    public int getGroup() {
        return Config.RA_GROUP;
    }


    public synchronized void releaseCS(){
        myts = Integer.MAX_VALUE;
        while (!pendingQ.isEmpty()){
            int pid = pendingQ.removeFirst();
            sendMessage(new Message(c.getValue(), Message.OKAY, myId, pid, getMyId()));
        }
        releaseHW(new Message(c.getValue(), Message.RELEASE, myId, Config.HWB_ID, getMyId()));
    }

    private void releaseHW(Message message){
        connection.sendRelease(message);
    }

    @Override
    public synchronized void handleMsg(Message m) {
        int timeStamp = m.getTimeStamp();
        c.receiveAction(m.getId(), timeStamp);
        if(m.getMessage().equals(Message.REQUEST)){
            if ( (myts == Integer.MAX_VALUE) || (timeStamp < myts) || ( (timeStamp == myts) && (m.getId() < myId)) ){
                sendMessage(new Message(c.getValue(), Message.OKAY, myId,m.getSrcId(), getMyId()));
            }else{
                pendingQ.add(m.getSrcId());
                //pendingQ.add(id);
            }
        }else if(m.getMessage().equals(Message.OKAY)){
            numOkay++;
        }
    }

    public void startConnection(int HWport, int [] LWports, int myPort)
    {
        if (Config.getVersion().equals(Config.VERSION0)){
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

    private void sendMessage(Message m){
        connection.sendMessage(m);
    }

    private void broadcast(Message m){
        connection.broadcast(m);
    }

    @Override
    public int getMyId(){
        return Config.RA_GROUP + myId;
    }

}
