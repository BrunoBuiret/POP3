package common.mail.exceptions;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class AlreadyMarkedForDeletionException extends MarkedForDeletionException
{
    /**
     * Creates a new <code>AlreadyMarkedForDeletionException</code> exception.
     *
     * @param s The detailed message.
     */
    public AlreadyMarkedForDeletionException(String s)
    {
        super(s);
    }
}
