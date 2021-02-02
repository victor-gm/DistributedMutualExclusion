package HeavyWeightA;

import Model.Config;

public class Main {
    public static void main(String[] args) {
        System.out.println("HeavyWeightA initiated");
        HeavyWeightA hwa = new HeavyWeightA();
        hwa.startLWServer(Config.HWAport);
        if(Config.getVersion().equals(Config.VERSION0)){
            hwa.openHWPort(Config.HWABport);
        }
        hwa.notifyLWServer();
        hwa.start();
    }
}
