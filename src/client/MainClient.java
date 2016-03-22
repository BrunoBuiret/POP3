package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
abstract class MainClient 
{
    public static void main(String[] args)
    {
        try
        {
            Pop3Client client = new Pop3Client(
                InetAddress.getByName("127.0.0.1"),
                110,
                "bruno.buiret",
                "bruno.buiret",
                "D:\\bruno.buiret.lmbox"
            );
            client.pop3();
        }
        catch(UnknownHostException ex)
        {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
