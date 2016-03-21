package common.mail.exceptions;

import java.io.IOException;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class FailedMailBoxUpdateException extends IOException
{
    /**
     * Creates a new <code>FailedMailBoxUpdateException</code> exception.
     * 
     * @param s The detailed message.
     */
    public FailedMailBoxUpdateException(String s)
    {
        super(s);
    }
}
