package server;

import common.Pop3Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.commands.AbstractPop3Command;

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
    public static final String DEFAULT_NAME = "<NAME>";
    
    /**
     * Default server port.
     */
    public static final int DEFAULT_PORT = 110;
    
    /**
     * Default path to the mailboxes.
     */
    public static final String DEFAULT_MAILBOXES_PATH = ".";
    
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
     * The server's path to its mailboxes directory.
     */
    protected String mailboxesPath;
    
    /**
     * The server' supported commands.
     */
    public Map<String, AbstractPop3Command> supportedCommands;
    
    /**
     * Creates a new POP3 server using the default name and port,
     * <code>Pop3Server.DEFAULT_NAME</code> and <code>Pop3Server.DEFAULT_PORT</code>.
     */
    public Pop3Server()
    {
        this(Pop3Server.DEFAULT_NAME, Pop3Server.DEFAULT_PORT, Pop3Server.DEFAULT_MAILBOXES_PATH, false);
    }
    
    /**
     * Creates a new POP3 server using a custom name and the default port,
     * <code>Pop3Server.DEFAULT_PORT</code>.
     * 
     * @param name The server's name.
     */
    public Pop3Server(String name)
    {
        this(name, Pop3Server.DEFAULT_PORT, Pop3Server.DEFAULT_MAILBOXES_PATH, false);
    }
    
    /**
     * Creates a new POP3 server using the default name and a custom port,
     * <code>Pop3Server.DEFAULT_NAME</code>.
     * 
     * @param port the server's port.
     */
    public Pop3Server(int port)
    {
        this(Pop3Server.DEFAULT_NAME, port, Pop3Server.DEFAULT_MAILBOXES_PATH, false);
    }
    
    /**
     * 
     * @param name
     * @param port 
     */
    public Pop3Server(String name, int port)
    {
        this(name, port, Pop3Server.DEFAULT_MAILBOXES_PATH, false);
    }
    
    /**
     * 
     * @param name
     * @param port 
     * @param mailboxesPath 
     */
    public Pop3Server(String name, int port, String mailboxesPath)
    {
        this(name, port, mailboxesPath, false);
    }
    
    /**
     * Creates a new POP3 server using a custom name and port.
     * 
     * @param name The server's name.
     * @param port The server's port.
     * @param debug The server's debug mode.
     * @param mailboxesPath
     */
    public Pop3Server(String name, int port, String mailboxesPath, boolean debug)
    {
        this.name = name;
        this.port = port;
        this.debug = debug;
        this.mailboxesPath = mailboxesPath;
        this.supportedCommands = new HashMap<>();
        
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
    
    /**
     * 
     * @param command
     * @return 
     */
    public AbstractPop3Command supportsCommand(String command)
    {
        return this.supportedCommands.containsKey(command)
            ? this.supportedCommands.get(command)
            : null
        ;
    }
}
