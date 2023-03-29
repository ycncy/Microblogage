package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MicroblogCentral {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            Socket client = serverSocket.accept();

            MicroblogHandler microblogHandler = new MicroblogHandler(client);
            executorService.submit(microblogHandler);
        }
    }
}
