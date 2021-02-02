package LightWeightB;

import Model.Config;
import RicartAgrawala.RAMutex;

public class LightWeightB2 {

    public static void main(String[] args)
    {
        int HWport = Config.HWBport;
        int[] LWports = {Config.LWB1port};
        int myPort = Config.LWB2port;

        RAMutex raMutex = new RAMutex(1);
        if(Config.getVersion().equals(Config.VERSION1)) HWport = Config.Router;
        raMutex.startConnection(HWport,LWports,myPort);
        //raMutex.ready();
        //raMutex.requestCS();
    }
}
