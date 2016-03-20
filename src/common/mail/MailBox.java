package common.mail;

import common.Rfc5322;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.exceptions.AlreadyMarkedForDeletionException;
import server.exceptions.FailedMailBoxUpdateException;
import server.exceptions.MarkedForDeletionException;
import server.exceptions.NonExistentMailException;
import server.exceptions.UnknownMailBoxException;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class MailBox
{
    /**
     * The mailbox's path.
     */
    protected String path;
    
    /**
     * The mailbox's list of mails.
     */
    protected List<Mail> mailsList;
    
    /**
     * The mailbox's list of mails to delete.
     */
    protected List<Mail> mailsToDeleteList;
    
    /**
     * Creates a new mailbox.
     * 
     * @param mailboxPath The mailbox's path.
     */
    public MailBox(String mailboxPath)
    {
        // Initialize properties
        this.path = mailboxPath;
        this.mailsList = new ArrayList<>();
        this.mailsToDeleteList = new ArrayList<>();
        
        // Test the mailbox
        File mailBoxFile = new File(this.path);
        
        // Does the mailbox exist?
        if(!mailBoxFile.exists())
        {
            throw new UnknownMailBoxException(String.format(
                "Mailbox \"%s\" doesn't exist.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Is it an actual file?
        if(!mailBoxFile.isFile())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" isn't an actual file.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Can it be read?
        if(!mailBoxFile.canRead())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" can't be read.",
                mailBoxFile.getAbsolutePath()
            ));
        }
    }
    
    /**
     * Gets the mailbox's path.
     * 
     * @return The mailbox's path.
     */
    public String getPath()
    {
        return this.path;
    }
    
    /**
     * Extracts the user's name from the mailbox's name.
     * 
     * @return The user's name.
     */
    public String getUserName()
    {
        File mailBoxFile = new File(this.path);
        String userName = mailBoxFile.getName();
        
        return userName.substring(0, userName.indexOf(".mbox"));
    }
    
    /**
     * Gets the number of mail in this mailbox.
     * 
     * @return The number of mails.
     */
    public int getSize()
    {
        return this.mailsList.size();
    }
    
    /**
     * Adds a mail to the mailbox.
     * 
     * @param mail The mail to add
     */
    public void add(Mail mail)
    {
        this.mailsList.add(mail);
    }
    
    /**
     * Gets one mail from the mailbox.
     * 
     * @param index The mail's index.
     * @return The wanted mail if it exists, <code>null</code> otherwise.
     * @throws NonExistentMailException If the mail associated to <code>index</code> doesn't exist.
     * @throws MarkedForDeletionException If the mail has been marked for deletion.
     */
    public Mail get(int index)
    {
        if(index < this.mailsList.size())
        {
            Mail mail = this.mailsList.get(index);
            
            if(!this.mailsToDeleteList.contains(mail))
            {
                return mail;
            }
            else
            {
                throw new MarkedForDeletionException(String.format(
                    "Mail #%d is marked for deletion.",
                    index
                ));
            }
        }
        else
        {
            throw new NonExistentMailException(String.format(
                "Mail #%d doesn't exist.",
                index
            ));
        }
    }
    
    /**
     * 
     * @return 
     */
    public List<Mail> getAll()
    {
        return this.mailsList;
    }
    
    /**
     * Marks a mail for deletion in this mailbox.
     * 
     * @param index The mail's index.
     * @throws NonExistentMailException If the mail associated to <code>index</code> doesn't exist.
     * @throws AlreadyMarkedForDeletionException If the mail is already marked for deletion.
     */
    public void delete(int index)
    {
        if(index < this.mailsList.size())
        {
            Mail mail = this.mailsList.get(index);
            
            if(!this.mailsToDeleteList.contains(mail))
            {
                this.mailsToDeleteList.add(mail);
            }
            else
            {
                throw new AlreadyMarkedForDeletionException(String.format(
                    "Mail #%d is already marked for deletion.",
                    index
                ));
            }
        }
        else
        {
            throw new NonExistentMailException(String.format(
                "Mail #%d doesn't exist.",
                index
            ));
        }
    }
    
    /**
     * Writes the mailbox's contents into its associated file.
     * 
     * @throws server.exceptions.FailedMailBoxUpdateException If the mailbox can't be saved.
     * @throws java.io.FileNotFoundException
     */
    public void save()
    throws FailedMailBoxUpdateException, FileNotFoundException
    {
        File mailBoxFile = new File(this.path);
        
        // If the mailbox exists, is it a file?
        if(mailBoxFile.exists() && !mailBoxFile.isFile())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" isn't a file.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Can it be written?
        if(!mailBoxFile.canWrite())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" can't be written.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Remove the emails marked for deletion but clone it first in case of a bug
        List<Mail> clonedMailsList = new ArrayList<>(this.mailsList);
        this.mailsList.removeAll(this.mailsToDeleteList);
        
        // Initialize vars
        BufferedOutputStream mailBoxStream = null;
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        Map<String, String> headers;
        
        try
        {
            // Try opening the mailbox
            mailBoxStream = new BufferedOutputStream(new FileOutputStream(mailBoxFile));
            
            for(Mail mail : this.mailsList)
            {
                // Write headers
                headers = mail.getHeaders();
                
                for(Map.Entry<String, String> entry : headers.entrySet())
                {
                    dataWriter.writeBytes(entry.getKey());
                    dataWriter.writeBytes(": ");
                    dataWriter.writeBytes(entry.getValue());
                    dataWriter.writeBytes("\r\n");
                }
                
                // Write separator
                dataWriter.writeBytes("\r\n");
                
                // Write body, first split it every 76 characters
                List<String> bodyFragments = new ArrayList<>();
                int currentIndex = 0, bodyLength = mail.getContents().length();
                
                while(currentIndex < bodyLength)
                {
                    if(bodyLength - currentIndex >= 76)
                    {
                        bodyFragments.add(
                            mail.getContents().substring(
                                currentIndex, currentIndex + 76
                            )
                        );
                        
                        currentIndex += 76;
                    }
                    else
                    {
                        bodyFragments.add(
                            mail.getContents().substring(
                                currentIndex
                            )
                        );
                        currentIndex = mail.getContents().length();
                    }
                }
                
                dataWriter.writeBytes(
                    String.join(
                        "\r\n",
                        bodyFragments
                    )
                );
                
                // End the body
                dataWriter.writeBytes("\r\n.\r\n");
            }
            
            // Write the emails into the file
            mailBoxStream.write(dataStream.toByteArray());
        }
        catch(FileNotFoundException ex)
        {
            // This error shouldn't happen because if the file doesn't exist, it'll be created
            throw ex;
        }
        catch(IOException ex)
        {
            // Reset the mails list
            this.mailsList = clonedMailsList;
            
            // Log the exception
            Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
            
            // Throw another exception
            FailedMailBoxUpdateException exception = new FailedMailBoxUpdateException(String.format(
                "Mailbox \"%s\" couldn't be saved.",
                mailBoxFile.getAbsolutePath()
            ));
            exception.addSuppressed(ex);
            
            throw exception;
        }
        finally
        {
            if(mailBoxStream != null)
            {
                try
                {
                    mailBoxStream.close();
                }
                catch(IOException ex)
                {
                    // Reset the mails list
                    this.mailsList = clonedMailsList;
                    
                    Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Reads the mailbox's contents from its associated file.
     * 
     * @throws java.io.FileNotFoundException
     */
    public void read()
    throws FileNotFoundException
    {
        File mailBoxFile = new File(this.path);
        
        // Does the mailbox exist?
        if(!mailBoxFile.exists())
        {
            throw new UnknownMailBoxException(String.format(
                "Mailbox \"%s\" doesn't exist.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Is it an actual file?
        if(!mailBoxFile.isFile())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" isn't an actual file.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Can it be read?
        if(!mailBoxFile.canRead())
        {
            throw new IllegalArgumentException(String.format(
                "Mailbox \"%s\" can't be read.",
                mailBoxFile.getAbsolutePath()
            ));
        }
        
        // Initialize vars
        BufferedInputStream mailBoxStream = null;
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int currentCharacter = -1, previousCharacter = -1;
        Mail mail;
        boolean endOfHeaders, endOfMail;
        
        try
        {
            // Try opening the mailbox
            mailBoxStream = new BufferedInputStream(new FileInputStream(mailBoxFile));
            
            while(mailBoxStream.available() > 0)
            {
                // Create a new mail
                mail = new Mail();
                endOfHeaders = endOfMail = false;
                
                // Read the headers
                do
                {
                    // Read the next character
                    previousCharacter = currentCharacter;
                    currentCharacter = mailBoxStream.read();

                    // Have we reached the end of a line?
                    if(currentCharacter == Rfc5322.ASCII_LF && previousCharacter == Rfc5322.ASCII_CR)
                    {
                        // Write the header
                        mail.addHeader(new String(dataStream.toByteArray(), StandardCharsets.ISO_8859_1).trim());

                        // And clear the output stream to start a new header
                        dataStream.reset();
                    }
                    // Ignore this character, we are going to reach the end of a line
                    else if(currentCharacter == Rfc5322.ASCII_CR && previousCharacter != Rfc5322.ASCII_LF)
                    {
                    }
                    // Have we reached the headers limit?
                    else if(currentCharacter == Rfc5322.ASCII_CR && previousCharacter == Rfc5322.ASCII_LF)
                    {
                        // Get rid of the next character, it must be an LF
                        mailBoxStream.read();
                        
                        // Stop this loop
                        endOfHeaders = true;
                    }
                    // Otherwise, simply add the current character to the output stream
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
                    currentCharacter = mailBoxStream.read();
                    
                    // Have we reached the end of the mail?
                    if(currentCharacter == Rfc5322.ASCII_POINT && previousCharacter == Rfc5322.ASCII_LF)
                    {
                        // Write the contents
                        mail.setContents(new String(dataStream.toByteArray(), StandardCharsets.ISO_8859_1).trim());
                        
                        // Get rid of the next two characters
                        mailBoxStream.read();
                        mailBoxStream.read();
                        
                        // And clear the output stream to start a new mail
                        dataStream.reset();
                        
                        // Then, stop this loop
                        endOfMail = true;
                    }
                    // Otherwise, simply add the current character to the output stream
                    else
                    {
                        dataWriter.writeByte(currentCharacter);
                    }
                }
                while(!endOfMail);
                
                // Save the email
                this.mailsList.add(mail);
            }
        }
        catch(FileNotFoundException ex)
        {
            // This error shouldn't happen because the mailbox's existence is tested
            throw ex;
        }
        catch (IOException ex)
        {
            Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(mailBoxStream != null)
            {
                try
                {
                    mailBoxStream.close();
                }
                catch(IOException ex)
                {
                    Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
