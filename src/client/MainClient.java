package client;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1411138
 */
abstract class MainClient 
{
    public static void main(String[] args)
    {
        int port = 110;
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("134.214.119.216");
        } catch (UnknownHostException ex) {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        Pop3Client client = new Pop3Client(ip, port, "thomas.arnaud", "thomas.arnaud", "C:\\Users\\Alexdef74307\\Desktop\\thomasarnaud.mbox");
        client.pop3();
    }
}
