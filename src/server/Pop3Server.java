package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import server.commands.AbstractPop3Command;
import server.commands.ApopCommand;
import server.commands.DeleCommand;
import server.commands.ListCommand;
import server.commands.PassCommand;
import server.commands.QuitCommand;
import server.commands.RetrCommand;
import server.commands.RsetCommand;
import server.commands.StatCommand;
import server.commands.UserCommand;

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
    protected SSLServerSocket socket;

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
     * The server's mailboxes' folder path.
     */
    protected String mailBoxesPath;

    /**
     * The server' secret used for the <code>APOP</code> command.
     */
    protected String secret;

    /**
     * The server' supported commands.
     */
    public Map<String, AbstractPop3Command> supportedCommands;

    /**
     * Creates a new POP3 server using the default name and port,
     * <code>Pop3Server.DEFAULT_NAME</code> and
     * <code>Pop3Server.DEFAULT_PORT</code>.
     */
    public Pop3Server()
    {
        this(
            Pop3Server.DEFAULT_NAME,
            Pop3Server.DEFAULT_PORT,
            Pop3Server.DEFAULT_MAILBOXES_PATH,
            false
        );
    }

    /**
     * Creates a new POP3 server using a custom name and the default port,
     * <code>Pop3Server.DEFAULT_PORT</code>.
     *
     * @param name The server's name.
     */
    public Pop3Server(String name)
    {
        this(
            name,
            Pop3Server.DEFAULT_PORT,
            Pop3Server.DEFAULT_MAILBOXES_PATH,
            false
        );
    }

    /**
     * Creates a new POP3 server using the default name and a custom port,
     * <code>Pop3Server.DEFAULT_NAME</code>.
     *
     * @param port The server's port.
     */
    public Pop3Server(int port)
    {
        this(
            Pop3Server.DEFAULT_NAME,
            port,
            Pop3Server.DEFAULT_MAILBOXES_PATH,
            false
        );
    }

    /**
     * Creates a new POP3 server using a custom name and custom port.
     *
     * @param name The server's name.
     * @param port The server's port.
     */
    public Pop3Server(String name, int port)
    {
        this(
            name,
            port,
            Pop3Server.DEFAULT_MAILBOXES_PATH,
            false
        );
    }

    /**
     * Creates a new POP3 server using a custom name and a custom port.
     *
     * @param name The server's name.
     * @param port The server's port.
     * @param mailboxesPath The mailboxes' folder path.
     */
    public Pop3Server(String name, int port, String mailboxesPath)
    {
        this(
            name,
            port,
            mailboxesPath,
            false
        );
    }

    /**
     * Creates a new POP3 server using a custom name and port.
     *
     * @param name The server's name.
     * @param port The server's port.
     * @param debug The server's debug mode.
     * @param mailboxesPath The mailboxes' folder path.
     */
    public Pop3Server(String name, int port, String mailboxesPath, boolean debug)
    {
        this.name = name;
        this.port = port;
        this.debug = debug;
        this.mailBoxesPath = mailboxesPath;
        this.supportedCommands = new HashMap<>();
        this.secret = "secret";

        // Register commands
        this.supportedCommands.put(
            "QUIT",
            new QuitCommand()
        );
        this.supportedCommands.put(
            "APOP",
            new ApopCommand()
        );
        this.supportedCommands.put(
            "USER",
            new UserCommand()
        );
        this.supportedCommands.put(
            "PASS",
            new PassCommand()
        );
        this.supportedCommands.put(
            "LIST",
            new ListCommand()
        );
        this.supportedCommands.put(
            "STAT",
            new StatCommand()
        );
        this.supportedCommands.put(
            "RETR",
            new RetrCommand()
        );
        this.supportedCommands.put(
            "DELE",
            new DeleCommand()
        );
        this.supportedCommands.put(
            "RSET",
            new RsetCommand()
        );

        // Start server
        try
        {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            //this.socket = new ServerSocket(this.port);
            this.socket = (SSLServerSocket) factory.createServerSocket(this.port);

            // Determine which cipher suites can be used
            List<String> cipherSuitesList = new ArrayList<>(Arrays.asList(this.socket.getSupportedCipherSuites()));
            List<String> usableCipherSuites = new ArrayList<>();

            for(String cipherSuite : cipherSuitesList)
            {
                if(cipherSuite.contains("anon"))
                {
                    usableCipherSuites.add(cipherSuite);
                }
            }

            this.socket.setEnabledCipherSuites(usableCipherSuites.toArray(new String[usableCipherSuites.size()]));
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
     * Gets the server's mailboxes' folder path.
     *
     * @return The server's mailboxes' folder path.
     */
    public String getMailBoxesPath()
    {
        return this.mailBoxesPath;
    }

    /**
     * Gets the secret used with the <code>APOP</code> command.
     *
     * @return The secret.
     */
    public String getSecret()
    {
        return this.secret;
    }

    /**
     * Gets a command if it is supported.
     *
     * @param command The command's name.
     * @return The command, <code>null</code> otherwise.
     */
    public AbstractPop3Command supportsCommand(String command)
    {
        return this.supportedCommands.containsKey(command)
            ? this.supportedCommands.get(command)
            : null;
    }
}
