package server;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class MainServer
{
    /**
     * Instantiates a new POP3 server.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        Pop3Server server = new Pop3Server(
            "etu.univ-lyon1.fr",
            Pop3Server.DEFAULT_PORT,
            "D:\\",
            true
        );
        server.run();
    }
}
