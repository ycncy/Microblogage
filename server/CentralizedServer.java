package server;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class CentralizedServer {

    private final static int port = 1234;

    public static Map<String, SocketChannel> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            Socket client = serverSocket.accept();

            CentralizedServerHandler centralizedServerHandler = new CentralizedServerHandler(client);
            executorService.submit(centralizedServerHandler);
        }
    }
}