
package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Thomas Arnaud
 */
public class Pop3Client 
{
    private Socket socket;
    private InetAddress ip;
    private int port;
    
    public Pop3Client(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
        
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
