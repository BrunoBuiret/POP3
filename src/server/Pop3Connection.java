package server;

import common.Pop3Protocol;
import common.Pop3State;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String request, command;
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
            responseBuilder.append(Pop3Protocol.END_OF_LINE);
            
            // Then, send it
            this.sendResponse(responseBuilder.toString());
            
            // Finally, clear the builder
            responseBuilder = null;
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Couldn't send greetings.",
                ex
            );
            
            // Close the socket
            this.close();
            
            // Then, finish the thread
            return;
        }
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
        
        try
        {
            // Try reading everything
            do
            {
                dataWriter.writeByte(this.socketReader.read());
            }
            while(this.socketReader.available() > 0);
            
            // Log if necessary
            if(this.server.isDebug())
            {
                Logger.getLogger(Pop3Server.class.getName()).log(
                    Level.INFO,
                    "<- {0}:{1} {2}",
                    new Object[]{this.socket.getInetAddress(), this.socket.getPort(), new String(dataStream.toByteArray()).trim()}
                );
            }
            
            return new String(dataStream.toByteArray());
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
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Connection.class.getName()).log(
                Level.SEVERE,
                "Can't send response to the client.",
                ex
            );
            
            throw ex;
        }
    }
    
    /**
     * Try closing the socket.
     */
    public void close()
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
}
