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
 * Implements the <code>STAT</code> POP3 command.
 *
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
            int mailsSize = 0, mailsNumber = 0;
            List<Mail> mailsList = mailBox.getAll();

            for(Mail mail : mailsList)
            {
                if(!mailBox.isDeleted(mail))
                {
                    mailsSize += mail.getSize();
                    mailsNumber++;
                }
            }

            try
            {
                // Inform the user of the mailbox's number of mais and total size
                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                responseBuilder.append(" ");
                responseBuilder.append(mailsNumber);
                responseBuilder.append(" ");
                responseBuilder.append(mailsSize);
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex)
            {
                Logger.getLogger(StatCommand.class.getName()).log(
                    Level.SEVERE,
                    "Statistics response couldn't be sent.",
                    ex
                );
            }
        }
        else
        {
            try
            {
                // Inform the user there are no associated mailbox
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" no mailbox associated");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex)
            {
                Logger.getLogger(StatCommand.class.getName()).log(
                    Level.SEVERE,
                    "Statistics response couldn't be sent.",
                    ex
                );
            }
        }

        return true;
    }
}
