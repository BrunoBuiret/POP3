package client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Pop3Client 
{
    private Socket socket;
    private InetAddress ipServer;
    private int portServer;
    private String user;
    
    public Pop3Client(InetAddress ipServer, int portServer, String user)
    {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.user = user;
        
        try {
            this.socket = new Socket(ipServer, portServer);
        } catch (IOException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.pop3();
    }

    public String read()
    {
        try
        {
            // Attente et lecture de la réponse
            InputStream input = socket.getInputStream();
            DataOutputStream writer = new DataOutputStream(new ByteArrayOutputStream());
            StringBuilder response = new StringBuilder();
            int readByte;
            
            // Lecture de l'en-tête
            do
            {
                // Lecture d'un octet
                writer.write(readByte = input.read());
                response.append((char) readByte);
            }
            while(input.available() > 0 && response.toString().lastIndexOf("\r\n\r\n") == -1);
            
            return response.toString();
        }
        catch(IOException e)
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }
                catch(IOException ex)
                {
                }
            }
        }
        
        return "error";
    }
    
    
    protected byte[] encodeResponse(String response)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int readByte;
        
        try
        { 
            // Ecriture de l'en-tête
            dataWriter.writeBytes(response);
   
            return dataStream.toByteArray();
        }
        catch(IOException e)
        {
            
        }
        
        return null;
    }
    
    protected void sendResponse(byte[] encodedResponse)
    {
        try
        {
            this.socket.getOutputStream().write(encodedResponse);
            this.socket.getOutputStream().flush();
        }
        catch(IOException e)
        {
        }
    } 
    
    /**
     * 
     * @return 
     */
    public int pop3()
    {
        if (!this.read().contains("+OK")) {
            return 0;
        }
        
        this.sendResponse(encodeResponse("USER " + this.user));
        return 0;
    }
}
