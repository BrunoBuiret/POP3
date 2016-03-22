package digest;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class DigestTest
{
    public static void main(String[] args)
    {
        // Initialize vars
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        int processId = Integer.parseInt(processName.substring(0, processName.indexOf("@")));
        String host = processName.substring(processName.indexOf("@") + 1);
        long clock = System.currentTimeMillis();
        
        System.out.print("PID: ");
        System.out.println(processId);
        System.out.print("Host: ");
        System.out.println(host);
        System.out.print("Clock: ");
        System.out.println(clock);
        
        try
        {
            String secret = "secret";
            String toHash = String.format("<%d.%d@%s>%s", processId, clock, host, secret);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hashed = md5.digest(toHash.getBytes(StandardCharsets.ISO_8859_1));
            
            System.out.print("Source: ");
            System.out.printf("<%d.%d@%s>%s", processId, clock, host, secret);
            System.out.println();
            System.out.print("Digested: ");
            
            for(byte b : hashed)
            {
                System.out.print(String.format(
                    "%02x",
                    b & 0xff
                ));
            }
            
            System.out.println();
        }
        catch(NoSuchAlgorithmException ex)
        {
            Logger.getLogger(DigestTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
