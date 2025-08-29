package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String HOST = "localhost";

    public static final int PORT = 19199;

    /*public static void acceptRequests(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                new Thread(() -> processSerializableClient(clientSocket));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }*/

    private static void processPrimitiveClient(Socket clientSocket){

    }
    private static void processSerializableClient(ServerSocket serverSocket){

    }

}
