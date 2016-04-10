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

    /**
     * Extracts the command name from a request.
     *
     * @param request The request to analyze.
     * @return The command name.
     */
    public static String extractCommand(String request)
    {
        return (request.contains(" ") ? request.substring(0, request.indexOf(" ")) : request).trim();
    }
}
