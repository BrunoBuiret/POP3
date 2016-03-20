package client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
public class Pop3Client {

    private Socket socket;
    private InetAddress ipServer;
    private int portServer;
    private String user;
    private boolean deletionParameter = false;
    private String password;
    
    private enum StateEnum {
        Initialisation,
        WaitForServer,
        WaitForUserNameValidation,
        WaitForPasswordValidation,
        WaitForReceptionConfirm,
        WaitForMessageReception,
        WaitForMessageDeletion;
    };
    
    private StateEnum stateEnum = StateEnum.Initialisation;

    public Pop3Client(InetAddress ipServer, int portServer, String user, String password) {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.user = user;
        this.password = password;

        try {
            this.socket = new Socket(ipServer, portServer);
        } catch (IOException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.pop3();
    }
    
    public Pop3Client(InetAddress ipServer, int portServer, String user, String password, boolean deletionParameter) {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.user = user;
        this.password = password;
        this.deletionParameter = deletionParameter;
        
        try {
            this.socket = new Socket(ipServer, portServer);
        } catch (IOException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.pop3();
    }

    public String read() {
        try {
            // Attente et lecture de la réponse
            InputStream input = socket.getInputStream();
            DataOutputStream writer = new DataOutputStream(new ByteArrayOutputStream());
            StringBuilder response = new StringBuilder();
            int readByte;

            // Lecture de l'en-tête
            do {
                // Lecture d'un octet
                writer.write(readByte = input.read());
                response.append((char) readByte);
            } while (input.available() > 0 && response.toString().lastIndexOf("\r\n\r\n") == -1);

            return response.toString();
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                }
            }
        }

        return "error";
    }

    protected byte[] encodeResponse(String response) {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int readByte;

        try {
            // Ecriture de l'en-tête
            dataWriter.writeBytes(response);

            return dataStream.toByteArray();
        } catch (IOException e) {

        }

        return null;
    }

    protected void sendResponse(byte[] encodedResponse) {
        try {
            this.socket.getOutputStream().write(encodedResponse);
            this.socket.getOutputStream().flush();
        } catch (IOException e) {
        }
    }

    protected int stateValidation(StateEnum futurState, String serverResponse) {
        if (!serverResponse.startsWith("+OK")) {
            System.out.println("Blocked at state " + this.stateEnum);
            return 0;
        }
        this.stateEnum = futurState;
        return -1;
    }
    
    
    public int pop3() {
        this.stateValidation(this.stateEnum.WaitForServer, this.read());

        this.sendResponse(encodeResponse("USER " + this.user));

        this.stateValidation(this.stateEnum.WaitForUserNameValidation, this.read());
        
        this.sendResponse(encodeResponse("PASS " + this.password));

        this.stateValidation(this.stateEnum.WaitForPasswordValidation, this.read());
        System.out.println("Connected with account " + this.user);

        this.sendResponse(encodeResponse("LIST"));

        String serverResponse = this.read();
        this.stateValidation(this.stateEnum.WaitForReceptionConfirm, serverResponse);

        int messageNbr = Integer.parseInt(serverResponse.split(" ")[1]);
        System.out.println("You have " + messageNbr + " messages.");

        for (int i = 0; i < messageNbr; i++) {
            this.sendResponse(encodeResponse("RETR " + i));
            serverResponse = this.read();
            this.stateValidation(this.stateEnum.WaitForMessageReception, serverResponse);
            String message = this.read();
            System.out.println(message);
            
            if (this.deletionParameter) {
                this.sendResponse(encodeResponse("DELE " + i));
            }
        }
        return 0;
    }
}
