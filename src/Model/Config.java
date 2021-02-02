package Model;

public class Config
{
    //A
    public static final int HWAport = 8080;
    public static final int LWA1port = 8081;
    public static final int LWA2port = 8082;
    public static final int LWA3port = 8083;

    //B
    public static final int HWBport = 8084;
    public static final int LWB1port = 8085;
    public static final int LWB2port = 8086;

    //AB
    public static final int HWABport = 8087;

    //Router
    public static final int Router = 8088;

    //IP
    public static final String LOCALHOST = "localhost";

    //Version
    public static final String VERSION0 = "V0";
    public static final String VERSION1 = "V1";

    //Lamport
    public static final int LAMPORT_NODES = 3;
    public static final char LAMPORT_GROUP = 'A';

    //R&A
    public static final int RA_NODES = 2;
    public static final char RA_GROUP = 'B' + 'M';

    //HWA
    public static final int HWA_ID = 'H'+'W'+'A';

    //HWAB
    public static final int HWB_ID = 'H'+'W'+'B';

    public static String getVersion(){
        return VERSION0;
    }
}
