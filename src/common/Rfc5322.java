package common;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class Rfc5322
{
    /**
     * The ASCII value for CR (<code>\r</code>).
     */
    public static final int ASCII_CR = 13;
    
    /**
     * The ASCII value for LF (<code>\n</code>).
     */
    public static final int ASCII_LF = 10;
    
    /**
     * The ASCII value for ".".
     */
    public static final int ASCII_POINT = 46;
    
    /**
     * 
     */
    public static final String END_OF_LINE = "\r\n";
}
