package protocol;

import java.io.IOException;
import java.net.ServerSocket;

public class MultipleClientProtocolServer implements Runnable {
    private ServerSocket serverSocket;
    private int listenPort;
    private ServerProtocolFactory GameProtocol;
    
    
    public MultipleClientProtocolServer(int port, ServerProtocolFactory GameProtocol)
    {
        serverSocket = null;
        listenPort = port;
        this.GameProtocol = GameProtocol;
    }
    
    public void run()
    {
        try {
            serverSocket = new ServerSocket(listenPort);
            System.out.println("Listening...");
        }
        catch (IOException e) {
            System.out.println("Cannot listen on port " + listenPort);
        }
        
        while (true)
        {
            try {
           ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(),GameProtocol.create());
            new Thread(newConnection).start();
            }
            catch (IOException e)
            {
                System.out.println("Failed to accept on port " + listenPort);
            }
        }
    }
    
 
    // Closes the connection
    public void close() throws IOException
    {
        serverSocket.close();
    }
    
    public static void main(String[] args) throws IOException
    {
        // Get port
        int port = Integer.decode(args[0]).intValue();
        
        MultipleClientProtocolServer server = new MultipleClientProtocolServer(port, new EchoProtocolFactory());
        Thread serverThread = new Thread(server);
      serverThread.start();
        try {
            serverThread.join();
        }
        catch (InterruptedException e)
        {
            System.out.println("Server stopped");
        }
        
        
                
    }
}
