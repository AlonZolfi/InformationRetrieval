package Client;

import java.io.InputStream;
import java.io.OutputStream;

public interface IClientStrategy {
    /**
     * performs a certain strategy
     * @param inFromServer inputstream of server
     * @param outToServer output stream of server
     */
    void clientStrategy(InputStream inFromServer, OutputStream outToServer);
}
