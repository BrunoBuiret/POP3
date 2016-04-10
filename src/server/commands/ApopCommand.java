package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.Mail;
import common.mail.MailBox;
import common.mail.exceptions.UnknownMailBoxException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;

/**
 * Implements the <code>APOP</code> POP3 command.
 *
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class ApopCommand extends AbstractPop3Command
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
     */
    @Override
    public boolean handle(Pop3Connection connection, String request)
    {
        // Initialize vars
        StringBuilder responseBuilder = new StringBuilder();

        if(null != connection.getSecurityDigest())
        {
            if(request.length() == 4
                || (request.length() > 4 && request.substring(4).trim().isEmpty()))
            {
                try
                {
                    // Inform the user they have to provide the username and the security digest
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" you have to provide the username and security digest");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex)
                {
                    Logger.getLogger(ApopCommand.class.getName()).log(
                        Level.SEVERE,
                        "Authentication response couldn't be sent.",
                        ex
                    );
                }
            }
            else
            {
                // Extract the arguments
                String[] arguments = request.substring(5).trim().split(" ");

                // Has the user provided everything?
                if(arguments.length == 2)
                {
                    try
                    {
                        // Try opening the mailbox and reading its contents
                        MailBox mailBox = new MailBox(
                            connection.getServer().getMailBoxesPath()
                            + File.separator
                            + arguments[0]
                            + ".mbox"
                        );
                        mailBox.canRead();

                        if(connection.getSecurityDigest().equals(arguments[1]))
                        {
                            // Attach the mailbox to the connection
                            connection.setMailBox(mailBox);

                            mailBox.read();

                            int mailsSize = 0;
                            List<Mail> mailsList = mailBox.getAll();

                            for(Mail mail : mailsList)
                            {
                                mailsSize += mail.getSize();
                            }

                            // Change the connection's current state
                            connection.setCurrentState(Pop3State.TRANSACTION);

                            // Inform the user the mailbox has been opened
                            responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                            responseBuilder.append(" maildrop has ");
                            responseBuilder.append(mailBox.getSize());
                            responseBuilder.append(" ");
                            responseBuilder.append(mailBox.getSize() > 1 ? "messages" : "message");
                            responseBuilder.append(" (");
                            responseBuilder.append(mailsSize);
                            responseBuilder.append(" ");
                            responseBuilder.append(mailsSize > 1 ? "octets" : "octet");
                            responseBuilder.append(")");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);
                        }
                        else
                        {
                            // Inform the user their security digest is invalid
                            responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                            responseBuilder.append(" ");
                            responseBuilder.append(arguments[0]);
                            responseBuilder.append(" security digest is invalid");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);
                        }

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(UnknownMailBoxException ex)
                    {
                        try
                        {
                            // Inform the user the mailbox doesn't exist
                            responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                            responseBuilder.append(" ");
                            responseBuilder.append(arguments[0]);
                            responseBuilder.append(" is an invalid mailbox");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);

                            connection.sendResponse(responseBuilder.toString());
                        }
                        catch(IOException ex1)
                        {
                            Logger.getLogger(ApopCommand.class.getName()).log(
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
                            responseBuilder.append(arguments[0]);
                            responseBuilder.append("'s mailbox can't be opened");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);

                            connection.sendResponse(responseBuilder.toString());
                        }
                        catch(IOException ex1)
                        {
                            Logger.getLogger(ApopCommand.class.getName()).log(
                                Level.SEVERE,
                                "Authentication response couldn't be sent.",
                                ex1
                            );
                        }
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(ApopCommand.class.getName()).log(
                            Level.SEVERE,
                            "Authentication response couldn't be sent.",
                            ex
                        );
                    }
                }
                else
                {
                    try
                    {
                        // Inform the user they have to provide both the username and the security digest
                        responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                        responseBuilder.append(" you have to provide both the username and security digest");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(ApopCommand.class.getName()).log(
                            Level.SEVERE,
                            "Authentication response couldn't be sent.",
                            ex
                        );
                    }
                }
            }
        }
        else
        {
            try
            {
                // Inform the user they can't use this command to authenticate themselves
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" you can't use APOP to authenticate yourself");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex)
            {
                Logger.getLogger(ApopCommand.class.getName()).log(
                    Level.SEVERE,
                    "Authentication response couldn't be sent.",
                    ex
                );
            }
        }

        return true;
    }
}
