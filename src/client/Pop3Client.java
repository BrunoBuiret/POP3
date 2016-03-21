package client;

import common.Pop3Protocol;
import common.mail.Mail;
import common.mail.MailBox;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.mail.exceptions.FailedMailBoxUpdateException;

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
    private MailBox mailbox;
    private int nbMessages = 0;
    
    private enum StateEnum {
        Initialisation,
        WaitForServer,
        WaitForUserNameValidation,
        WaitForPasswordValidation,
        WaitForReceptionConfirm,
        WaitForMessageReception,
        WaitForMessageDeletion,
        WaitForExitConfirm;
    };
    
    private StateEnum stateEnum = StateEnum.Initialisation;

    public Pop3Client(InetAddress ipServer, int portServer, String user, String password, String path) {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.user = user;
        this.password = password;
        this.mailbox = new MailBox(path);

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
        return 1;
    }
    
    public int user(String username) {
        this.sendResponse(encodeResponse("USER " + username + Pop3Protocol.END_OF_LINE));
        
        return this.stateValidation(this.stateEnum.WaitForUserNameValidation, this.read());
    }
            
    public int pass(String password) {
        this.sendResponse(encodeResponse("PASS " + password + Pop3Protocol.END_OF_LINE));
        int is_valid = this.stateValidation(this.stateEnum.WaitForPasswordValidation, this.read());
        System.out.println("Connected with account " + this.user);
        return is_valid;
    }
    
    //todo
    public int list() {
        this.sendResponse(encodeResponse("LIST" + Pop3Protocol.END_OF_LINE));
        String serverResponse = this.read();
        int messageNbr = Integer.parseInt(serverResponse.split(" ")[1]);
        
        System.out.println("You have " + messageNbr + " messages.");
        for(int i = 0; i < messageNbr; i++) {
            System.out.println(this.read());
        }
        this.nbMessages = messageNbr;
        
        return this.stateValidation(this.stateEnum.WaitForReceptionConfirm, serverResponse);
    }
    
    public int retrieve(int i) {
        this.sendResponse(encodeResponse("RETR " + i));
        String serverResponse = this.read();
        int is_valid = this.stateValidation(this.stateEnum.WaitForMessageReception, serverResponse);
        String message = this.read();
        Mail mail = new Mail();
        
        try {
            mail.addHeader(message.split("<CR><LF>\n<CR><LF>")[0]);
            mail.setContents(message.split("<CR><LF>\n<CR><LF>")[1]);
            this.mailbox.save();
        } catch (FailedMailBoxUpdateException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return is_valid;
    }
    
    public int delete(int i) {
        this.sendResponse(encodeResponse("DELE " + i));
        return this.stateValidation(this.stateEnum.WaitForMessageDeletion, this.read());
    }
    
    public int quit() {
        this.sendResponse(encodeResponse("QUIT "));
        return this.stateValidation(this.stateEnum.WaitForExitConfirm, this.read());
    }
    
    public int pop3() {
        this.stateValidation(this.stateEnum.WaitForServer, this.read());

        if(user(this.user) != 0) {
            return -1;
        }
        
        if(pass(this.password) != 0) {
            return -2;
        }
        
        if(list() != 0) {
            return -3;
        }
        
        for (int i = 0; i < this.nbMessages; i++) {
            if(retrieve(i) != 0) {
                return -4;
            }
            if(this.deletionParameter) {
                if(delete(i) != 0) {
                    return -5;
                }
            }
        }
        
        if(quit() != 0) {
            return -6;
        }
        
        return 0;
    }
}
