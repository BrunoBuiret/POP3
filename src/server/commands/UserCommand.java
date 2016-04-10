package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.MailBox;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;
import common.mail.exceptions.UnknownMailBoxException;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class UserCommand extends AbstractPop3Command
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Pop3Connection connection)
    {
        return Pop3State.AUTHORIZATION == connection.getCurrentState();
    }

    /**
     * {@inheritDoc}
     *
     * @todo Test the mailbox exists there and not in the constructor
     */
    @Override
    public boolean handle(Pop3Connection connection, String request)
    {
        // Initialize vars
        StringBuilder responseBuilder = new StringBuilder();

        // Has the user's name been given?
        if(request.length() == 4
            || (request.length() > 4 && request.substring(4).trim().isEmpty()))
        {
            try
            {
                // Inform the user they have to provide the username
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" you have to provide the username");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex)
            {
                Logger.getLogger(UserCommand.class.getName()).log(
                    Level.SEVERE,
                    "Authentication response couldn't be sent.",
                    ex
                );
            }
        }
        else
        {
            // Extract the user's name from the request
            String userName = request.substring(5).trim();

            try
            {
                // Try opening the mailbox and reading its contents
                MailBox mailBox = new MailBox(
                    connection.getServer().getMailBoxesPath()
                    + File.separator
                    + userName
                    + ".mbox"
                );
                mailBox.canRead();

                // Attach the mailbox to the connection
                connection.setMailBox(mailBox);

                // Inform the user the mailbox exists
                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                responseBuilder.append(" ");
                responseBuilder.append(userName);
                responseBuilder.append(" is a valid mailbox");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(UnknownMailBoxException ex)
            {
                try
                {
                    // Inform the user the mailbox doesn't exist
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" ");
                    responseBuilder.append(userName);
                    responseBuilder.append(" is an invalid mailbox");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex1)
                {
                    Logger.getLogger(UserCommand.class.getName()).log(
                        Level.SEVERE,
                        "Authentication response couldn't be sent.",
                        ex1
                    );
                }
            }
            catch(IllegalArgumentException ex)
            {
                try
                {
                    // Inform the user the mailbox couldn't be opened
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" ");
                    responseBuilder.append(userName);
                    responseBuilder.append("'s mailbox can't be opened");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex1)
                {
                    Logger.getLogger(UserCommand.class.getName()).log(
                        Level.SEVERE,
                        "Authentication response couldn't be sent.",
                        ex1
                    );
                }
            }
            catch(IOException ex)
            {
                Logger.getLogger(UserCommand.class.getName()).log(
                    Level.SEVERE,
                    "Authentication response couldn't be sent.",
                    ex
                );
            }
        }

        return true;
    }
}
