package client;

import java.io.InputStream;
import java.net.InetAddress;

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
        Pop3Client client = new Pop3Client(ip, port, "Thomas");
    }
}
