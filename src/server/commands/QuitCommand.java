package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.MailBox;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;
import common.mail.exceptions.FailedMailBoxUpdateException;

/**
 * Implements the <code>QUIT</code> POP3 command.
 *
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class QuitCommand extends AbstractPop3Command
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Pop3Connection connection)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(Pop3Connection connection, String request)
    {
        // Initialize vars
        StringBuilder responseBuilder = new StringBuilder();
        MailBox mailBox = connection.getMailBox();

        // Change connection's current state
        connection.setCurrentState(Pop3State.UPDATE);

        if(null != mailBox)
        {
            try
            {
                // First, actually delete the emails
                mailBox.save();

                // Then, inform the user
                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                responseBuilder.append(" ");
                responseBuilder.append(connection.getServer().getName());
                responseBuilder.append(" POP3 server signing off (");

                if(mailBox.getSize() > 0)
                {
                    responseBuilder.append(mailBox.getSize());
                    responseBuilder.append(" ");
                    responseBuilder.append(mailBox.getSize() > 1 ? "messages" : "message");
                }
                else
                {
                    responseBuilder.append("mailbox empty");
                }

                responseBuilder.append(")");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(FailedMailBoxUpdateException ex)
            {
                try
                {
                    // The update has failed, inform the user
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" some deleted messages not removed");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex1)
                {
                    Logger.getLogger(QuitCommand.class.getName()).log(
                            Level.SEVERE,
                            "Signing off response couldn't be sent to the user.",
                            ex1
                    );
                }
            }
            catch(IOException ex)
            {
                Logger.getLogger(QuitCommand.class.getName()).log(
                        Level.SEVERE,
                        "Signing off response couldn't be sent to the user.",
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
            catch(IOException ex1)
            {
                Logger.getLogger(QuitCommand.class.getName()).log(
                        Level.SEVERE,
                        "Signing off response couldn't be sent to the user.",
                        ex1
                );
            }
        }

        return false;
    }
}
