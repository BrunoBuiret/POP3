package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Pop3Server
{
    /**
     * Default server name.
     */
    public static final String DEFAULT_NAME = "NAME";
    
    /**
     * Default server port.
     */
    public static final int DEFAULT_PORT = 110;
    
    /**
     * The server' socket.
     */
    protected ServerSocket socket;
    
    /**
     * The server's name.
     */
    protected String name;
    
    /**
     * The server's port.
     */
    protected int port;
    
    /**
     * The server's debug mode.
     */
    protected boolean debug;
    
    /**
     * Creates a new POP3 server using the default name and port,
     * <code>Pop3Server.DEFAULT_NAME</code> and <code>Pop3Server.DEFAULT_PORT</code>.
     */
    public Pop3Server()
    {
        this(Pop3Server.DEFAULT_NAME, Pop3Server.DEFAULT_PORT, false);
    }
    
    /**
     * Creates a new POP3 server using a custom name and the default port,
     * <code>Pop3Server.DEFAULT_PORT</code>.
     * 
     * @param name The server's name.
     */
    public Pop3Server(String name)
    {
        this(name, Pop3Server.DEFAULT_PORT, false);
    }
    
    /**
     * Creates a new POP3 server using the default name and a custom port,
     * <code>Pop3Server.DEFAULT_NAME</code>.
     * 
     * @param port the server's port.
     */
    public Pop3Server(int port)
    {
        this(Pop3Server.DEFAULT_NAME, port, false);
    }
    
    /**
     * Creates a new POP3 server using a custom name and port.
     * 
     * @param name The server's name.
     * @param port The server's port.
     * @param debug The server's debug mode.
     */
    public Pop3Server(String name, int port, boolean debug)
    {
        this.name = name;
        this.port = port;
        this.debug = debug;
        
        try
        {
            this.socket = new ServerSocket(this.port);
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Server.class.getName()).log(
                Level.SEVERE,
                "Couldn't start server socket.",
                ex
            );
        }
    }
    
    /**
     * Launches the server and runs it.
     */
    public void run()
    {
        while(true)
        {
            try
            {
                Pop3Connection connection = new Pop3Connection(this, this.socket.accept());
                connection.start();
            }
            catch(IOException ex)
            {
                Logger.getLogger(Pop3Server.class.getName()).log(
                    Level.SEVERE,
                    "Cannot accept new connection.",
                    ex
                );
            }
        }
    }
    
    /**
     * Gets the server's name.
     * 
     * @return The server's name.
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Gets the server's port.
     * 
     * @return The server's port.
     */
    public int getPort()
    {
        return this.port;
    }
    
    /**
     * Gets the server's debug mode.
     * 
     * @return The server's debug mode.
     */
    public boolean isDebug()
    {
        return this.debug;
    }
}
