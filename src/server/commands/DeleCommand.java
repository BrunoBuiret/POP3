package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.MailBox;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;
import server.exceptions.AlreadyMarkedForDeletionException;
import server.exceptions.NonExistentMailException;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class DeleCommand extends AbstractPop3Command
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
        
        // Has the user's name been given?
        if(
            request.length() == 4
            || (request.length() > 4 && request.substring(4).trim().isEmpty())
        )
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
                Logger.getLogger(DeleCommand.class.getName()).log(
                    Level.SEVERE,
                    "Deletion response couldn't be sent.",
                    ex
                );
            }
        }
        else
        {
            try
            {
                // Extract the mail's index from the request
                int index = Integer.parseInt(request.substring(5).trim()) - 1;
                
                try
                {
                    // Try marking the mail for deletion
                    connection.getMailBox().delete(index);
                    
                    try
                    {
                        // Inform the user the mail has been marked for deletion
                        responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                        responseBuilder.append(" message ");
                        responseBuilder.append(index);
                        responseBuilder.append(" deleted");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(DeleCommand.class.getName()).log(
                            Level.SEVERE,
                            "Deletion response couldn't be sent.",
                            ex
                        );
                    }
                }
                catch(AlreadyMarkedForDeletionException ex)
                {
                    try
                    {
                        // Inform the user the mail has already been marked for deletion
                        responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                        responseBuilder.append(" message ");
                        responseBuilder.append(index);
                        responseBuilder.append(" already deleted");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        connection.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex1)
                    {
                        Logger.getLogger(DeleCommand.class.getName()).log(
                            Level.SEVERE,
                            "Deletion response couldn't be sent.",
                            ex
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
                        Logger.getLogger(DeleCommand.class.getName()).log(
                            Level.SEVERE,
                            "Deletion response couldn't be sent.",
                            ex
                        );
                    }
                }
            }
            catch(NumberFormatException ex)
            {
                try
                {
                    // Inform the user they have to provide the username
                    responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                    responseBuilder.append(" couldn't extract mail index");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex1)
                {
                    Logger.getLogger(DeleCommand.class.getName()).log(
                        Level.SEVERE,
                        "Deletion response couldn't be sent.",
                        ex
                    );
                }
            }
        }
        
        return true;
    }
}
