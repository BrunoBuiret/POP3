package common;

/**
 * @author Bruno Buiret <bruno.buiret@etu.univ-lyon1.fr>
 * @author Thomas Arnaud <thomas.arnaud@etu.univ-lyon1.fr>
 * @author Alexis Rabilloud <alexis.rabilloud@etu.univ-lyon1.fr>
 */
public enum Pop3State
{
    /**
     * The thread has just been started.
     */
    INITIALIZATION,
    /**
     * Waiting for authentication from the user.
     */
    AUTHORIZATION,
    /**
     * User has been authenticated.
     */
    TRANSACTION,
    /**
     * User is logging out of the server.
     */
    UPDATE;
}
