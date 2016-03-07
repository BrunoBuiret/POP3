package server.commands;

import server.Pop3Connection;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public abstract class AbstractPop3Command
{
    /**
     * Tests if the command can be used at this moment.
     * 
     * @param connection A reference to the connection.
     * @return <code>true</code> if the command is valid, <code>false</code> otherwise.
     */
    public abstract boolean isValid(Pop3Connection connection);
    
    /**
     * Handles the request.
     * 
     * @param request The request to handle.
     */
    public abstract void handle(String request);
}
