package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.Mail;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;
import common.mail.exceptions.MarkedForDeletionException;
import common.mail.exceptions.NonExistentMailException;

/**
 * Implements the <code>RETR</code> POP3 command.
 *
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class RetrCommand extends AbstractPop3Command
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
        StringBuilder responseBuilder = new StringBuilder();

        if(null != connection.getMailBox())
        {
            // Has the mail's index been given?
            if(request.length() == 4
                    || (request.length() > 4 && request.substring(4).trim().isEmpty()))
            {
                try
                {
                    // Inform the user they have to provide the mail's index
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" you have to provide the mail's index");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex)
                {
                    Logger.getLogger(RetrCommand.class.getName()).log(
                            Level.SEVERE,
                            "Retrieval response couldn't be sent.",
                            ex
                    );
                }
            }
            else
            {
                try
                {
                    // Extract the mail's index from the request
                    int index = Integer.parseInt(request.substring(5).trim());

                    if(index > 0)
                    {
                        try
                        {
                            // Try fetching the mail
                            Mail mail = connection.getMailBox().get(index - 1);

                            try
                            {
                                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                                responseBuilder.append(" ");
                                responseBuilder.append(mail.getSize());
                                responseBuilder.append(" ");
                                responseBuilder.append(mail.getSize() > 1 ? "octets" : "octet");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                for(Map.Entry<String, String> entry : mail.getHeaders().entrySet())
                                {
                                    responseBuilder.append(entry.getKey());
                                    responseBuilder.append(": ");
                                    responseBuilder.append(entry.getValue());
                                    responseBuilder.append(Pop3Protocol.END_OF_LINE);
                                }

                                responseBuilder.append(Pop3Protocol.END_OF_LINE);
                                responseBuilder.append(mail.getContents());
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);
                                responseBuilder.append(".");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex)
                            {
                                Logger.getLogger(RetrCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Retrieval response couldn't be sent.",
                                        ex
                                );
                            }
                        }
                        catch(MarkedForDeletionException ex)
                        {
                            try
                            {
                                // Inform the user the mail has been marked for deletion
                                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                                responseBuilder.append(" message ");
                                responseBuilder.append(index);
                                responseBuilder.append(" deleted");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(RetrCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Retrieval response couldn't be sent.",
                                        ex1
                                );
                            }
                        }
                        catch(NonExistentMailException ex)
                        {
                            try
                            {
                                // Inform the user the mail doesn't exist
                                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                                responseBuilder.append(" no such message");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(RetrCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Retrieval response couldn't be sent.",
                                        ex1
                                );
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            // Inform the user the index is invalid
                            responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                            responseBuilder.append(" invalid mail number");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);

                            connection.sendResponse(responseBuilder.toString());
                        }
                        catch(IOException ex)
                        {
                            Logger.getLogger(RetrCommand.class.getName()).log(
                                    Level.SEVERE,
                                    "Retrieval response couldn't be sent.",
                                    ex
                            );
                        }
                    }
                }
                catch(NumberFormatException ex)
                {
                    try
                    {
                        // Inform the user they have to provide the mail's index
                        responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                        responseBuilder.append(" couldn't extract mail index");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex1)
                    {
                        Logger.getLogger(RetrCommand.class.getName()).log(
                                Level.SEVERE,
                                "Retrieval response couldn't be sent.",
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
            catch(IOException ex)
            {
                Logger.getLogger(RetrCommand.class.getName()).log(
                        Level.SEVERE,
                        "Retrieval response couldn't be sent.",
                        ex
                );
            }
        }

        return true;
    }
}
