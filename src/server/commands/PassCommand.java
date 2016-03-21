package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.Mail;
import common.mail.MailBox;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;
import common.mail.exceptions.UnknownMailBoxException;

/**
 * Implements the <code>PASS</code> POP3 command.
 * 
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class PassCommand extends AbstractPop3Command
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

        if(null != connection.getMailBox())
        {
            // Has the password been given?
            if(
                request.length() == 4
                || (request.length() > 4 && request.substring(4).trim().isEmpty())
            )
            {
                try
                {
                    // Remove the mailbox
                    connection.setMailBox(null);

                    // Inform the user they have provide the password
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" you have to provide the password");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex)
                {
                    Logger.getLogger(PassCommand.class.getName()).log(
                        Level.SEVERE,
                        "Authentication response couldn't be sent.",
                        ex
                    );
                }
            }
            else
            {
                // Extract the password from the request
                String password = request.substring(5).trim();
                MailBox mailBox = connection.getMailBox();

                if(null != mailBox)
                {
                    // Is the password correct for this mailbox?
                    if(mailBox.getUserName().equals(password)) // @todo Find a better password, maybe, write it at the top of the file?
                    {
                        try
                        {
                            // @todo Lock the mailbox

                            // Try reading the mailbox's contents
                            mailBox.read();
                            int mailsSize = 0;
                            List<Mail> mailsList = mailBox.getAll();

                            for(Mail mail : mailsList)
                            {
                                mailsSize += mail.getSize();
                            }

                            // Change the connection's current state
                            connection.setCurrentState(Pop3State.TRANSACTION);

                            try
                            {
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

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(PassCommand.class.getName()).log(
                                    Level.SEVERE,
                                    "Authentication response couldn't be sent.",
                                    ex1
                                );
                            }
                        }
                        catch(FileNotFoundException | UnknownMailBoxException ex)
                        {
                            try
                            {
                                // Remove the mailbox
                                connection.setMailBox(null);

                                // Inform the user the mailbox doesn't exist
                                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                                responseBuilder.append(" ");
                                responseBuilder.append(mailBox.getUserName());
                                responseBuilder.append(" is now an invalid mailbox");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(PassCommand.class.getName()).log(
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
                                // Remove the mailbox
                                connection.setMailBox(null);

                                // Inform the user the mailbox couldn't be opened
                                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                                responseBuilder.append(" unable to open mailbox");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(PassCommand.class.getName()).log(
                                    Level.SEVERE,
                                    "Authentication response couldn't be sent.",
                                    ex1
                                );
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            // Remove the mailbox
                            connection.setMailBox(null);

                            // Inform the user, their password is invalid
                            responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                            responseBuilder.append(" invalid password");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);

                            connection.sendResponse(responseBuilder.toString());
                        }
                        catch(IOException ex)
                        {
                            Logger.getLogger(PassCommand.class.getName()).log(
                                Level.SEVERE,
                                "Authentication response couldn't be sent.",
                                ex
                            );
                        }
                    }
                }
                else
                {
                    try
                    {
                        responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                        responseBuilder.append(" you have to provide the username before");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(PassCommand.class.getName()).log(
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
                // Inform the user there are no associated mailbox
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" no mailbox associated");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);
                
                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex1)
            {
                Logger.getLogger(PassCommand.class.getName()).log(
                    Level.SEVERE,
                    "Authentication response couldn't be sent.",
                    ex1
                );
            }
        }

        return true;
    }
}
