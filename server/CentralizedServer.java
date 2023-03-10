package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralizedServer {

    private final static int port = 1234;

    public CentralizedServer(int port) {
        //this.port = port;
    }

    public static void run() throws IOException, RuntimeException {

        //ENTETE : PUBLISH author:@user \r\n corps \r\n

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}