package common.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Mailbox
{
    /**
     * The ASCII value for CR (<code>\r</code>).
     */
    protected static int ASCII_CR = 13;
    
    /**
     * The ASCII value for LF (<code>\n</code>).
     */
    protected static int ASCII_LF = 10;
    
    /**
     * The ASCII value for ".".
     */
    protected static int ASCII_POINT = 46;
    
    /**
     * The mailbox's path.
     */
    protected String path;
    
    /**
     * 
     */
    protected String userName;
    
    /**
     * 
     */
    protected List<Mail> mailsList;
    
    /**
     * Creates a new mailbox.
     * 
     * @param mailboxPath The mailbox's path.
     */
    public Mailbox(String mailboxPath)
    {
        this.mailsList = new ArrayList<>();
        this.readMailbox();
    }
    
    /**
     * 
     * @return 
     */
    public int size()
    {
        return this.mailsList.size();
    }
    
    /**
     * 
     * @param mail 
     */
    public void add(Mail mail)
    {
        this.mailsList.add(mail);
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public Mail get(int index)
    {
        return index < this.mailsList.size() ? this.mailsList.get(index) : null;
    }
    
    /**
     * 
     * @param index 
     */
    public void delete(int index)
    {
        if(index < this.mailsList.size())
        {
            this.mailsList.remove(index);
            this.writeMailbox();
        }
    }
    
    /**
     * Writes the mailbox's contents into its associated file.
     */
    protected void writeMailbox()
    {
        
    }
    
    /**
     * Reads the mailbox's contents from its associated file.
     */
    protected void readMailbox()
    {
        File mailboxFile = new File(this.path);
        
        // Does the mailbox exist?
        if(!mailboxFile.exists())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" doesn't exist.",
                this.path
            ));
        }
        
        // Is it an actual file?
        if(!mailboxFile.isFile())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" isn't an actual file.",
                this.path
            ));
        }
        
        // Can it be read?
        if(!mailboxFile.canRead())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" can't be read.",
                this.path
            ));
        }
        
        // Try reading the mailbox
        BufferedInputStream mailboxStream = null;
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int currentCharacter = -1, previousCharacter = -1;
        Mail mail;
        boolean endOfHeaders, endOfMail;
        
        try
        {
            mailboxStream = new BufferedInputStream(new FileInputStream(mailboxFile));
            
            while(mailboxStream.available() > 0)
            {
                // Create a new mail
                mail = new Mail();
                endOfHeaders = endOfMail = false;
                
                // Read the headers
                do
                {
                    // Read the next character
                    previousCharacter = currentCharacter;
                    currentCharacter = mailboxStream.read();

                    // Have we reached the end of a line?
                    if(currentCharacter == Mailbox.ASCII_LF && previousCharacter == Mailbox.ASCII_CR)
                    {
                        // Write the header
                        mail.addHeader(new String(dataStream.toByteArray()));

                        // And clear the output stream to start a new header
                        dataStream.reset();
                    }
                    // Ignore this character, we are going to reach the end of a line
                    else if(currentCharacter == Mailbox.ASCII_CR && previousCharacter != Mailbox.ASCII_LF)
                    {
                    }
                    // Have we reached the headers limit?
                    else if(currentCharacter == Mailbox.ASCII_CR && previousCharacter == Mailbox.ASCII_LF)
                    {
                        // Get rid of the next character, it must be an LF
                        mailboxStream.read();
                        
                        // Stop this loop
                        endOfHeaders = true;
                    }
                    // Simply add the current character to the output stream
                    else
                    {
                        dataWriter.writeByte(currentCharacter);
                    }
                }
                while(!endOfHeaders);
                
                // Read the contents
                do
                {
                    // Read the next character
                    previousCharacter = currentCharacter;
                    currentCharacter = mailboxStream.read();
                    
                    // Have we reached the end of the mail?
                    if(currentCharacter == Mailbox.ASCII_POINT && previousCharacter == Mailbox.ASCII_LF)
                    {
                        // Write the contents
                        mail.setContents(new String(dataStream.toByteArray()));
                        
                        // Get rid of the next two characters
                        mailboxStream.read();
                        mailboxStream.read();
                        
                        // And clear the output stream to start a new mail
                        dataStream.reset();
                        
                        // Then, stop this loop
                        endOfMail = true;
                    }
                    // Simply add the current character to the output stream
                    else
                    {
                        dataWriter.writeByte(currentCharacter);
                    }
                }
                while(!endOfMail);
            }
        }
        catch(FileNotFoundException ex)
        {
            Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(mailboxStream != null)
            {
                try
                {
                    mailboxStream.close();
                }
                catch(IOException ex)
                {
                    Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
