package common.mail;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Mail
{
    /**
     * The mail's headers list.
     */
    protected Map<String, String> headersList;

    /**
     * The mail's contents.
     */
    protected String contents;

    /**
     * Creates a new empty mail.
     */
    public Mail()
    {
        this.headersList = new HashMap<>();
        this.contents = null;
    }

    /**
     * Gets a header's value from the mail.
     *
     * @param headerName The header's name.
     * @return The header's value if it exists, <code>null</code> otherwise.
     */
    public String getHeader(String headerName)
    {
        return this.headersList.getOrDefault(headerName, null);
    }

    /**
     * Gets every headers from the mail.
     *
     * @return The headers.
     */
    public Map<String, String> getHeaders()
    {
        return this.headersList;
    }

    /**
     * Adds an header to the mail.
     *
     * @param header A string containing both the header's name and the header's
     * value.
     */
    public void addHeader(String header)
    {
        int pos = header.indexOf(":");

        this.headersList.put(header.substring(0, pos), header.substring(pos + 2));
    }

    /**
     * Adds an header to the mail.
     *
     * @param name The header's name.
     * @param value The header's value.
     */
    public void addHeader(String name, String value)
    {
        this.headersList.put(name, value);
    }

    /**
     * Gets the mail's contents.
     *
     * @return The mail's contents.
     */
    public String getContents()
    {
        return this.contents;
    }

    /**
     * Sets the mail's contents.
     *
     * @param contents The mail's contents.
     */
    public void setContents(String contents)
    {
        this.contents = contents;
    }

    /**
     * Gets the mail' size.
     *
     * @return The mail' size.
     */
    public int getSize()
    {
        int size = 0;

        // Compute headers' length
        for(Map.Entry<String, String> entry : this.headersList.entrySet())
        {
            size += entry.getKey().getBytes(StandardCharsets.ISO_8859_1).length;
            size += 2; // ": "
            size += entry.getValue().getBytes(StandardCharsets.ISO_8859_1).length;
            size += 2; // "CRLF"
        }

        size += 2; // "CRLF"

        // Add the body's length
        size += this.contents.getBytes(StandardCharsets.ISO_8859_1).length;

        return size;
    }
}
