package server;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class MainServer
{
    /**
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        Pop3Server server = new Pop3Server(Pop3Server.DEFAULT_NAME, Pop3Server.DEFAULT_PORT, true);
        server.run();
    }
}
