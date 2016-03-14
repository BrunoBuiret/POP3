package common.mail;

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
     * 
     */
    protected Map<String, String> headersList;
    
    /**
     * 
     */
    protected String contents;
    
    /**
     * 
     */
    public Mail()
    {
        this.headersList = new HashMap<>();
        this.contents = null;
    }
    
    /**
     * 
     * @param header 
     */
    public void addHeader(String header)
    {
        int pos = header.indexOf(":");
        
        this.headersList.put(header.substring(0, pos), header.substring(pos + 1));
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public void addHeader(String name, String value)
    {
        this.headersList.put(name, value);
    }
    
    /**
     * 
     * @param headerName
     * @return 
     */
    public String getHeader(String headerName)
    {
        return this.headersList.containsKey(headerName) ? this.headersList.get(headerName) : null;
    }
    
    /**
     * 
     * @return 
     */
    public Map<String, String> getHeaders()
    {
        return this.headersList;
    }
    
    /**
     * 
     * @return 
     */
    public String getContents()
    {
        return this.contents;
    }
    
    /**
     * 
     * @param contents 
     */
    public void setContents(String contents)
    {
        this.contents = contents;
    }
}
