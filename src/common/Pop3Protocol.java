package common;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class Pop3Protocol
{
    /**
     * POP3' success indicator.
     */
    public static final String MESSAGE_OK = "+OK";
    
    /**
     * POP3's error indicator.
     */
    public static final String MESSAGE_ERROR = "-ERR";
    
    /**
     * A request's or response's end of line.
     */
    public static final String END_OF_LINE = "\r\n";
    
    public static final String COMMAND_APOP = "APOP";
    
    public static final String COMMAND_USER = "USER";
    
    public static final String COMMAND_PASSWORD = "PASS";
    
    public static final String COMMAND_QUIT = "QUIT";
    
    public static final String COMMAND_LIST = "LIST";
    
    public static final String COMMAND_STATISTICS = "STAT";
    
    public static final String COMMAND_RETRIEVE = "RETR";
    
    public static final String COMMAND_DELETE = "DELE";
}
