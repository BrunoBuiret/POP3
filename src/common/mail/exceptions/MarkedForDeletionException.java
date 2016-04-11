package common.mail.exceptions;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class MarkedForDeletionException extends IllegalArgumentException
{

    /**
     * Creates a new <code>MailMarkedForDeletionException</code> exception.
     *
     * @param s The detailed message.
     */
    public MarkedForDeletionException(String s)
    {
        super(s);
    }
}
