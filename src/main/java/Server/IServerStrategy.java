package Server;

import java.io.InputStream;
import java.io.OutputStream;

public interface IServerStrategy {
    /**
     * performs the strategy of the server
     * @param inFromClient - input stream of the client
     * @param outToClient - output stream of the client
     */
    void serverStrategy(InputStream inFromClient, OutputStream outToClient);
}