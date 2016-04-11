package client;

import common.Pop3Protocol;
import common.mail.Mail;
import common.mail.MailBox;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.mail.exceptions.FailedMailBoxUpdateException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class Pop3Client
{

    protected enum StateEnum
    {
        Initialisation,
        WaitForServer,
        WaitForUserNameValidation,
        WaitForPasswordValidation,
        WaitForApopValidation,
        WaitForReceptionConfirm,
        WaitForMessageReception,
        WaitForMessageDeletion,
        WaitForExitConfirm;
    };

    protected Socket socket;
    protected InetAddress ipServer;
    protected int portServer;
    protected boolean deletionParameter = true;
    protected MailBox mailbox;
    protected int messagesNumber = 0;
    protected String userName = null;
    protected StateEnum stateEnum = StateEnum.Initialisation;
    protected BufferedOutputStream socketWriter;
    protected BufferedInputStream socketReader;
    protected String secret = "secret";
    protected String securityDigest;

    /**
     *
     * @param ipServer
     * @param portServer
     * @param path
     */
    public Pop3Client(InetAddress ipServer, int portServer, String path)
    {
        this(ipServer, portServer, path, false);
    }

    /**
     *
     * @param ipServer
     * @param portServer
     * @param path
     * @param deletionParameter
     */
    public Pop3Client(InetAddress ipServer, int portServer, String path, boolean deletionParameter)
    {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.mailbox = new MailBox(path);
        this.deletionParameter = deletionParameter;

        try
        {
            // Create socket and fetch reader and writer
            this.socket = new Socket(ipServer, portServer);
            this.socketReader = new BufferedInputStream(this.socket.getInputStream());
            this.socketWriter = new BufferedOutputStream(this.socket.getOutputStream());

            // Read greetings
            String greetingsResponse = this.readResponse();
            this.stateValidation(StateEnum.WaitForServer, greetingsResponse);

            if(greetingsResponse.contains("<"))
            {
                try
                {
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    StringBuilder digestBuilder = new StringBuilder();
                    byte[] rawSecurityDigest = md5.digest((greetingsResponse.substring(
                        greetingsResponse.indexOf("<"),
                        greetingsResponse.indexOf(">") + 1
                    )
                        + this.secret).getBytes(StandardCharsets.ISO_8859_1));

                    for(byte b : rawSecurityDigest)
                    {
                        digestBuilder.append(String.format("%02x", b & 0xff));
                    }

                    this.securityDigest = digestBuilder.toString();
                }
                catch(NoSuchAlgorithmException ex)
                {
                    this.securityDigest = null;
                }
            }
            else
            {
                this.securityDigest = null;
            }
        }
        catch(IOException ex)
        {
            Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    protected String readResponse()
    {
        try
        {
            // Attente et lecture de la réponse
            ByteArrayOutputStream dataStream;
            DataOutputStream writer = new DataOutputStream(dataStream = new ByteArrayOutputStream());
            int readByte;

            // Lecture de l'en-tête
            do
            {
                // Lecture d'un octet
                readByte = this.socketReader.read();

                if(readByte != -1)
                {
                    writer.writeByte(readByte);
                }
            }
            while(this.socketReader.available() > 0 && readByte != -1);

            return new String(dataStream.toByteArray(), StandardCharsets.ISO_8859_1);

        }
        catch(IOException e)
        {
            if(this.socket != null)
            {
                try
                {
                    this.socket.close();
                }
                catch(IOException ex)
                {
                }
            }
        }

        return null;
    }

    /**
     *
     * @param request
     * @return
     */
    protected byte[] encodeRequest(String request)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        int readByte;

        try
        {
            // Ecriture de l'en-tête
            dataWriter.writeBytes(request);

            return dataStream.toByteArray();
        }
        catch(IOException e)
        {

        }

        return null;
    }

    /**
     *
     * @param encodedRequest
     */
    protected void sendRequest(byte[] encodedRequest)
    {
        try
        {
            this.socketWriter.write(encodedRequest);
            this.socketWriter.flush();
        }
        catch(IOException e)
        {
        }
    }

    /**
     *
     * @param futurState
     * @param serverResponse
     * @return
     */
    protected int stateValidation(StateEnum futurState, String serverResponse)
    {
        if(!serverResponse.startsWith("+OK"))
        {
            return 0;
        }

        this.stateEnum = futurState;

        return 1;
    }

    /**
     * Sends a <code>USER</code> command.
     *
     * @param userName The username to use.
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int user(String userName)
    {
        this.userName = userName;
        this.sendRequest(this.encodeRequest("USER " + userName + Pop3Protocol.END_OF_LINE));

        return this.stateValidation(StateEnum.WaitForUserNameValidation, this.readResponse());
    }

    /**
     * Gets the last username.
     *
     * @return The last username if {@link #user(java.lang.String)} has already
     * been called, <code>null</code> otherwise.
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * Sends a <code>PASS</code> command.
     *
     * @param password The password to use.
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int pass(String password)
    {
        this.sendRequest(this.encodeRequest("PASS " + password + Pop3Protocol.END_OF_LINE));

        return this.stateValidation(StateEnum.WaitForPasswordValidation, this.readResponse());
    }

    /**
     * Sends a <code>APOP</code> command.
     *
     * @param userName The username to use.
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int apop(String userName)
    {
        if(null != this.securityDigest)
        {
            this.sendRequest(this.encodeRequest("APOP " + userName + " " + this.securityDigest + Pop3Protocol.END_OF_LINE));

            return this.stateValidation(StateEnum.WaitForApopValidation, this.readResponse());
        }

        return 0;
    }

    /**
     * Sends a <code>LIST</code> command.
     *
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int list()
    {
        this.sendRequest(this.encodeRequest("LIST" + Pop3Protocol.END_OF_LINE));
        String serverResponse = this.readResponse();
        int isValid = this.stateValidation(StateEnum.WaitForReceptionConfirm, serverResponse);

        if(1 == isValid)
        {
            this.messagesNumber = Integer.parseInt(serverResponse.split(" ")[3]);
        }

        return isValid;
    }

    /**
     * Gets the last number of messages.
     *
     * @return The last number of messages.
     */
    public int getMessagesNumber()
    {
        return this.messagesNumber;
    }

    /**
     * Sends a <code>RETR</code> command.
     *
     * @param index The email index.
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int retrieve(int index)
    {
        this.sendRequest(this.encodeRequest("RETR " + index + Pop3Protocol.END_OF_LINE));
        StringBuilder serverResponseBuilder = new StringBuilder();
        serverResponseBuilder.append(this.readResponse());
        serverResponseBuilder.append(this.readResponse());
        String serverResponse = serverResponseBuilder.toString();
        int isValid = this.stateValidation(StateEnum.WaitForMessageReception, serverResponse);

        if(1 == isValid)
        {
            try
            {
                Mail mail = new Mail();
                String rawMail = serverResponse.substring(serverResponse.indexOf(Pop3Protocol.END_OF_LINE) + 2);
                String[] mailHeaders = rawMail.split(Pop3Protocol.END_OF_LINE + Pop3Protocol.END_OF_LINE)[0].split(Pop3Protocol.END_OF_LINE);
                String mailBody = rawMail.split(Pop3Protocol.END_OF_LINE + Pop3Protocol.END_OF_LINE)[1];

                for(String mailHeader : mailHeaders)
                {
                    mail.addHeader(mailHeader);
                }

                mail.setContents(mailBody.substring(0, mailBody.indexOf(Pop3Protocol.END_OF_LINE + "." + Pop3Protocol.END_OF_LINE)));

                this.mailbox.add(mail);
                this.mailbox.save();
            }
            catch(FailedMailBoxUpdateException | FileNotFoundException ex)
            {
                Logger.getLogger(Pop3Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isValid;
    }

    /**
     * Gets the associated local mailbox.
     *
     * @return The associated local mailbox.
     */
    public MailBox getMailBox()
    {
        return this.mailbox;
    }

    /**
     * Sends a <code>DELE</code> command.
     *
     * @param index The email index.
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int delete(int index)
    {
        this.sendRequest(this.encodeRequest("DELE " + index + Pop3Protocol.END_OF_LINE));

        return this.stateValidation(StateEnum.WaitForMessageDeletion, this.readResponse());
    }

    /**
     * Sends a <code>QUIT</code> command.
     *
     * @return 1 if the request is successful, 0 otherwise.
     */
    public int quit()
    {
        this.sendRequest(this.encodeRequest("QUIT " + Pop3Protocol.END_OF_LINE));

        return this.stateValidation(StateEnum.WaitForExitConfirm, this.readResponse());
    }

    /**
     * Executes a typical scenario.
     *
     * @return 0 if everything was correct, an error code otherwise.
     */
    public int scenario()
    {
        if(this.user("bruno.buiret") == 0)
        {
            return -1;
        }

        if(this.pass("bruno.buiret") == 0)
        {
            return -2;
        }

        if(this.list() == 0)
        {
            return -3;
        }

        for(int i = 0; i < this.messagesNumber; i++)
        {
            if(this.retrieve(i + 1) == 0)
            {
                return -4;
            }

            if(this.deletionParameter)
            {
                if(this.delete(i + 1) == 0)
                {
                    return -5;
                }
            }
        }

        if(this.quit() == 0)
        {
            return -6;
        }

        return 0;
    }
}
