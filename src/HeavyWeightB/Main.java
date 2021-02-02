package HeavyWeightB;

import HeavyWeightA.HeavyWeightA;
import Model.Config;
import Model.Token;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {
        System.out.println("HeavyWeightB initiated");
        Token token = new Token(false);
        HeavyWeightB hwb = new HeavyWeightB(token);
        hwb.startLWServer(Config.HWBport);
        if(Config.getVersion().equals(Config.VERSION0)) {
            hwb.connectToHWA(Config.HWABport);
        }
        System.out.println("Waiting for token");
        while(!token.getTokenValue()){

            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        hwb.notifyLWServer();

    }
}
