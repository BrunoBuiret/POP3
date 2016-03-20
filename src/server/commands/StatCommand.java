package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.Mail;
import common.mail.MailBox;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class StatCommand extends AbstractPop3Command
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Pop3Connection connection)
    {
        return Pop3State.TRANSACTION == connection.getCurrentState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(Pop3Connection connection, String request)
    {
        // Initialize vars
        MailBox mailBox = connection.getMailBox();
        StringBuilder responseBuilder = new StringBuilder();
        
        if(null != mailBox)
        {
            // Try reading the mailbox's contents
            int mailsSize = 0;
            List<Mail> mailsList = mailBox.getAll();

            for(Mail mail : mailsList)
            {
                mailsSize += mail.getSize();
            }
            
            try
            {
                // Inform the user the mailbox has been opened
                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                responseBuilder.append(" ");
                responseBuilder.append(mailBox.getSize());
                responseBuilder.append(" ");
                responseBuilder.append(mailsSize);
                responseBuilder.append(Pop3Protocol.END_OF_LINE);
                
                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex1)
            {
                Logger.getLogger(UserCommand.class.getName()).log(
                    Level.SEVERE,
                    "Messages list couldn't be sent.",
                    ex1
                );
            }
        }
        else
        {
            try
            {
                // Inform the user the mailbox has been opened
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" no mailbox associated");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);
                
                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex1)
            {
                Logger.getLogger(UserCommand.class.getName()).log(
                    Level.SEVERE,
                    "Messages list couldn't be sent.",
                    ex1
                );
            }
        }
        
        return true;
    }
}
