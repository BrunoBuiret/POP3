package mail;

import common.mail.Mail;
import common.mail.MailBox;
import java.util.Map;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class MailboxTest
{
    public static void main(String[] args)
    {
        MailBox mailBox = new MailBox("D:\\bruno.buiret.mbox");
        
        // Some statistics
        System.out.println("Nombre de mails présents : " + mailBox.getSize());
        
        // Read every mail
        Mail mail;
        Map<String, String> headers;
        
        for(int i = 0, j = mailBox.getSize(); i < j; i++)
        {
            mail = mailBox.get(i);
            headers = mail.getHeaders();
            
            // Print headers
            for(Map.Entry<String, String> entry : headers.entrySet())
            {
                System.out.println(
                    String.format(
                        "%s: %s",
                        entry.getKey(),
                        entry.getValue()
                    )
                );
            }
            
            // Print body
            System.out.println("---");
            System.out.println(mail.getContents());
            System.out.println("---");
        }
    }
}
