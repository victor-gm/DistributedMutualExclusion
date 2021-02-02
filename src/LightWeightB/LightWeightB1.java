package LightWeightB;

import Model.Config;
import RicartAgrawala.RAMutex;

public class LightWeightB1 {

    public static void main(String[] args)
    {
        int HWport = Config.HWBport;
        int[] LWports = {Config.LWB2port};
        int myPort = Config.LWB1port;

        RAMutex raMutex = new RAMutex(0);
        if(Config.getVersion().equals(Config.VERSION1)) HWport = Config.Router;
        raMutex.startConnection(HWport,LWports,myPort);
        //raMutex.ready();
        //raMutex.requestCS();
    }
}
