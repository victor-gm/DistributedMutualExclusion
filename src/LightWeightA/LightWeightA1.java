package LightWeightA;

import Lamport.LamportMutex;
import Model.Config;

public class LightWeightA1
{
    public static void main(String[] args)
    {
        String version = Config.getVersion();
        int HWport = Config.HWAport;
        int[] LWports = {Config.LWA2port, Config.LWA3port};
        int myPort = Config.LWA1port;

        LamportMutex lamportMutex = new LamportMutex(0);
        if(version.equals(Config.VERSION1)) HWport = Config.Router;
        lamportMutex.startConnection(HWport,LWports,myPort);
        //lamportMutex.ready();
        //lamportMutex.requestCS();
    }

}
