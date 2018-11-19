package Client;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private InetAddress serverIP;
    private int serverPort;
    private IClientStrategy clientStrategy;

    /**
     * Creates a new instance of a client
     * @param serverIP server IP
     * @param serverPort server Port
     * @param clientStrategy client's strategy
     */
    public Client(InetAddress serverIP, int serverPort, IClientStrategy clientStrategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientStrategy = clientStrategy;
    }

    public void communicateWithServer() {
        try {
            Socket theServer = new Socket(serverIP, serverPort);//opens a port to the server
            System.out.println(String.format("Client is connected to server (IP: %s, port: %s)", serverIP, serverPort));
            clientStrategy.clientStrategy(theServer.getInputStream(), theServer.getOutputStream());//perform the strategy of the client
            theServer.close();//close connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
