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
}
