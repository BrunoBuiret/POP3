package server;

import common.Pop3Protocol;
import common.Pop3State;
import common.mail.MailBox;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.commands.AbstractPop3Command;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Pop3Connection extends Thread
{
    /**
     * The connection's link to the server.
     */
    protected Pop3Server server;

    /**
     * The connection' socket.
     */
    protected Socket socket;

    /**
     * The connection's output stream.
     */
    protected BufferedOutputStream socketWriter;

    /**
     * The connection's input stream.
     */
    protected BufferedInputStream socketReader;

    /**
     * The connection's current state.
     */
    protected Pop3State currentState;

    /**
     *
     */
    protected MailBox mailbox;
    
    /**
     * 
     */
    protected String securityDigest;

    /**
     * Creates a new POP3 connection.
     *
     * @param server A reference to the server.
     * @param socket The newly created socket.
     */
    public Pop3Connection(Pop3Server server, Socket socket)
    {
        // Initialize the connection's properties
        this.server = server;
        this.socket = socket;
        this.currentState = Pop3State.INITIALIZATION;

        // Try getting the socket's different streams
        try
        {
            this.socketWriter = new BufferedOutputStream(this.socket.getOutputStream());
            this.socketReader = new BufferedInputStream(this.socket.getInputStream());
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Couldn't get the socket' streams.",
                ex
            );
        }
    }

    /**
     *
     */
    @Override
    public void run()
    {
        // Initialize some vars
        StringBuilder responseBuilder;

        // Indicate the connection has been established
        try
        {
            // Build the response
            responseBuilder = new StringBuilder();
            responseBuilder.append(Pop3Protocol.MESSAGE_OK);
            responseBuilder.append(" ");
            responseBuilder.append(this.server.getName());
            responseBuilder.append(" POP3 server ready");
            
            // Compute security digest if possible
            try
            {
                // Initialize vars
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                String processName = ManagementFactory.getRuntimeMXBean().getName();
                int processId = Integer.parseInt(processName.substring(0, processName.indexOf("@")));
                String host = processName.substring(processName.indexOf("@") + 1);
                long clock = System.currentTimeMillis();
                
                // Build security digest
                StringBuilder digestBuilder = new StringBuilder();
                byte[] rawSecurityDigest = md5.digest(
                    String.format(
                        "<%d.%d@%s>%s",
                        processId,
                        clock,
                        host,
                        this.server.getSecret()
                    ).getBytes(StandardCharsets.ISO_8859_1)
                );
                
                for(byte b : rawSecurityDigest)
                {
                    digestBuilder.append(String.format("%02x", b & 0xff));
                }
                
                this.securityDigest = digestBuilder.toString();
                
                // Add the data needed to build the security digest for the client
                responseBuilder.append(" <");
                responseBuilder.append(processId);
                responseBuilder.append(".");
                responseBuilder.append(clock);
                responseBuilder.append("@");
                responseBuilder.append(host);
                responseBuilder.append(">");
            }
            catch (NoSuchAlgorithmException ex)
            {
                // MD5 message digest couldn't be fetched, disable 
                this.securityDigest = null;
                
                Logger.getLogger(Pop3Connection.class.getName()).log(
                    Level.SEVERE,
                    "Couldn't fetch MD5 message digest.",
                    ex
                );
            }
            
            responseBuilder.append(Pop3Protocol.END_OF_LINE);

            // Then, send it
            this.sendResponse(responseBuilder.toString());

            // Finally, clear the builder
            responseBuilder = null;

            // And set the state
            this.currentState = Pop3State.AUTHORIZATION;
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Couldn't send greetings.",
                ex
            );

            // Close the socket
            this.closeSocket();

            // Then, finish the thread
            return;
        }

        // Main loop
        String request;
        boolean keepLooping = true;
        AbstractPop3Command command;

        do
        {
            // Read the client's request
            request = this.readRequest();

            if(null != request && !request.isEmpty())
            {
                command = this.server.supportsCommand(Pop3Protocol.extractCommand(request));

                // Is the command supported?
                if(null != command)
                {
                    if(command.isValid(this))
                    {
                        // Handle the command
                        keepLooping = command.handle(this, request);
                    }
                    else
                    {
                        // The command is invalid because it can't be used right now
                        try
                        {
                            // Build the error response
                            responseBuilder = new StringBuilder();
                            responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                            responseBuilder.append(" invalid command");
                            responseBuilder.append(Pop3Protocol.END_OF_LINE);

                            // Then, send it
                            this.sendResponse(responseBuilder.toString());
                        }
                        catch(IOException ex)
                        {
                            Logger.getLogger(Pop3Connection.class.getName()).log(
                                Level.SEVERE,
                                "Couldn't send error response.",
                                ex
                            );
                        }
                        finally
                        {
                            // Finally, clear the builder
                            responseBuilder = null;
                        }
                    }
                }
                else
                {
                    try
                    {
                        // Build the error response
                        responseBuilder = new StringBuilder();
                        responseBuilder.append(Pop3Protocol.MESSAGE_ERROR);
                        responseBuilder.append(" unknown command");
                        responseBuilder.append(Pop3Protocol.END_OF_LINE);

                        // Then, send it
                        this.sendResponse(responseBuilder.toString());
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(Pop3Connection.class.getName()).log(
                            Level.SEVERE,
                            "Couldn't send error response.",
                            ex
                        );
                    }
                    finally
                    {
                        // Finally, clear the builder
                        responseBuilder = null;
                    }
                }
            }
        }
        while(keepLooping);

        // The loop has reached its end, close the socket and end the thread
        this.closeSocket();
    }

    /**
     * Reads a request sent by the client.
     *
     * @return The request.
     */
    protected String readRequest()
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int readByte;

        try
        {
            // Try reading everything
            do
            {
                readByte = this.socketReader.read();
                dataWriter.writeByte(readByte);
            }
            while(this.socketReader.available() > 0 && readByte != -1);
            
            if(dataStream.size() == 0)
            {
                return null;
            }

            // Log if necessary
            if(this.server.isDebug())
            {
                Logger.getLogger(Pop3Server.class.getName()).log(
                    Level.INFO,
                    "<- {0}:{1} {2}",
                    new Object[]{this.socket.getInetAddress(), this.socket.getPort(), new String(dataStream.toByteArray()).trim()}
                );
            }

            return new String(dataStream.toByteArray()).trim();
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Server.class.getName()).log(
                Level.SEVERE,
                "Couldn't read request from client.",
                ex
            );
        }

        return null;
    }

    /**
     * Sends a response to the client.
     *
     * @param response The response to send.
     * @throws IOException Thrown when the response can't be sent.
     */
    public void sendResponse(String response)
    throws IOException
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());

        try
        {
            // Transform the response into a byte array
            dataWriter.writeBytes(response);

            // Log if necessary
            if(this.server.isDebug())
            {
                Logger.getLogger(Pop3Server.class.getName()).log(
                    Level.INFO,
                    "-> {0}:{1} {2}",
                    new Object[]{this.socket.getInetAddress(), this.socket.getPort(), response.trim()}
                );
            }

            // Then, send the response to the client
            this.socketWriter.write(dataStream.toByteArray());
            this.socketWriter.flush();
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Couldn't send response to the client.",
                ex
            );

            throw ex;
        }
    }

    /**
     * Try closing the socket.
     */
    public void closeSocket()
    {
        try
        {
            this.socket.close();
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Couldn't close socket.",
                ex
            );
        }
    }

    /**
     * Gets a connection's reference to the server.
     *
     * @return The server.
     */
    public Pop3Server getServer()
    {
        return this.server;
    }

    /**
     * Gets a connection's current state.
     *
     * @return The current state.
     */
    public Pop3State getCurrentState()
    {
        return this.currentState;
    }

    /**
     * Sets a connection's current state.
     *
     * @param state The state.
     */
    public void setCurrentState(Pop3State state)
    {
        this.currentState = state;
    }

    /**
     *
     * @return
     */
    public MailBox getMailBox()
    {
        return this.mailbox;
    }

    /**
     *
     * @param mailBox
     */
    public void setMailBox(MailBox mailBox)
    {
        this.mailbox = mailBox;
    }
    
    /**
     * 
     * @return 
     */
    public String getSecurityDigest()
    {
        return this.securityDigest;
    }
}
