package server.commands;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.Mail;
import common.mail.MailBox;
import common.mail.exceptions.MarkedForDeletionException;
import common.mail.exceptions.NonExistentMailException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Pop3Connection;

/**
 * Implements the <code>LIST</code> POP3 command.
 *
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class ListCommand extends AbstractPop3Command
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
            // Is there an argument?
            if(request.length() > 4)
            {
                try
                {
                    int index = Integer.parseInt(request.substring(5).trim());

                    if(index > 0)
                    {
                        try
                        {
                            // Try fetching the mail
                            Mail mail = connection.getMailBox().get(index - 1);

                            try
                            {
                                // Inform the user the mail has been marked for deletion
                                responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                                responseBuilder.append(" ");
                                responseBuilder.append(index);
                                responseBuilder.append(" ");
                                responseBuilder.append(mail.getSize());
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex)
                            {
                                Logger.getLogger(ListCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Mails list response couldn't be sent.",
                                        ex
                                );
                            }
                        }
                        catch(MarkedForDeletionException ex)
                        {
                            try
                            {
                                // Inform the user the mail has already been marked for deletion
                                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                                responseBuilder.append(" message ");
                                responseBuilder.append(index);
                                responseBuilder.append(" deleted");
                                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                                connection.sendResponse(responseBuilder.toString());
                            }
                            catch(IOException ex1)
                            {
                                Logger.getLogger(ListCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Mails list response couldn't be sent.",
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
                                Logger.getLogger(ListCommand.class.getName()).log(
                                        Level.SEVERE,
                                        "Mails list response couldn't be sent.",
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
                            Logger.getLogger(ListCommand.class.getName()).log(
                                    Level.SEVERE,
                                    "Mails list response couldn't be sent.",
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
                        Logger.getLogger(ListCommand.class.getName()).log(
                                Level.SEVERE,
                                "Mails list response couldn't be sent.",
                                ex
                        );
                    }
                }
            }
            else
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
                    // Inform the user the mailbox has been opened
                    responseBuilder.append(Pop3Protocol.MESSAGE_OK);
                    responseBuilder.append(" maildrop has ");
                    responseBuilder.append(mailsNumber);
                    responseBuilder.append(" ");
                    responseBuilder.append(mailsNumber > 1 ? "messages" : "message");
                    responseBuilder.append(" (");
                    responseBuilder.append(mailsSize);
                    responseBuilder.append(" ");
                    responseBuilder.append(mailsSize > 1 ? "octets" : "octet");
                    responseBuilder.append(")");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    for(int i = 0, j = mailsList.size(); i < j; i++)
                    {
                        if(!mailBox.isDeleted(mailsList.get(i)))
                        {
                            responseBuilder.append(i + 1);
                            responseBuilder.append(" ");
                            responseBuilder.append(mailsList.get(i).getSize());
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);
                        }
                    }

                    responseBuilder.append(".");
                    responseBuilder.append(Pop3Protocol.END_OF_LINE);

                    connection.sendResponse(responseBuilder.toString());
                }
                catch(IOException ex)
                {
                    Logger.getLogger(ListCommand.class.getName()).log(
                            Level.SEVERE,
                            "Mails list response couldn't be sent.",
                            ex
                    );
                }
            }
        }
        else
        {
            try
            {
                // Inform the user there are no mailbox associated
                responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                responseBuilder.append(" no mailbox associated");
                responseBuilder.append(Pop3Protocol.END_OF_LINE);

                connection.sendResponse(responseBuilder.toString());
            }
            catch(IOException ex)
            {
                Logger.getLogger(ListCommand.class.getName()).log(
                        Level.SEVERE,
                        "Mails list response couldn't be sent.",
                        ex
                );
            }
        }

        return true;
    }
}
