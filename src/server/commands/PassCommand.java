package server.commands;

import common.Pop3State;
import server.Pop3Connection;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public class PassCommand extends AbstractPop3Command
{
    /**
     * {@inheritDoc}
     * @todo Also test if there was a <code>USER</code> command sent right before.
     */
    @Override
    public boolean isValid(Pop3Connection connection)
    {
        return Pop3State.AUTHORIZATION == connection.getCurrentState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(Pop3Connection connection, String request)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
