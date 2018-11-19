package Server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private int listeningInterval;
    private IServerStrategy serverStrategy;
    private volatile boolean stop;
    private ExecutorService pool;
    private ServerSocket server = null;
    //  private Socket client = null;

    /**
     * Initializes new server
     *
     * @param port              port of the server
     * @param listeningInterval timeout interval
     * @param serverStrategy    strategy of the server
     */
    public Server(int port, int listeningInterval, IServerStrategy serverStrategy) {
        this.port = port;
        this.listeningInterval = listeningInterval;
        this.serverStrategy = serverStrategy;
        this.stop = false;
        this.pool = Executors.newFixedThreadPool(5);
    }


    public void start() {
        new Thread(() -> {
            runServer();
        }).start();//run the server on a new thread
    }

    /**
     * Method to run the server with
     */
    private void runServer() {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(listeningInterval);
            while (!stop) {
                try {
                    Socket client = server.accept(); // blocking call
                    pool.execute(new Runnable() {//once a client is accepted add him to the pool
                        @Override
                        public void run() {
                            handleClient(client);
                        }
                    });
                } catch (SocketTimeoutException e) {
                }
            }
            pool.shutdown();//after stop was submitted close the pool
            server.close();//close the server
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * handle the client and perform strategy on the client
     *
     * @param clientSocket the client to communicate with
     */
    private void handleClient(Socket clientSocket) {
        try {
            //perform the strategy of the server with the client details
            serverStrategy.serverStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the server
     */
    public void stop() {
        stop = true;
    }

}