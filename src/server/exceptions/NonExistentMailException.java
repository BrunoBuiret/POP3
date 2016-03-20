package server.exceptions;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class NonExistentMailException extends IndexOutOfBoundsException
{
    /**
     * Creates a new <code>NonExistentMailException</code> exception.
     * 
     * @param s The detailed message.
     */
    public NonExistentMailException(String s)
    {
        super(s);
    }
}
