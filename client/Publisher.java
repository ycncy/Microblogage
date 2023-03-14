package client;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Publisher {

    private static final String connectionAddress = "localhost";
    private static final int connectionPort = 1234;

    public static void main(String[] args) {
        try {
            SocketChannel client = SocketChannel.open(new InetSocketAddress(connectionAddress, connectionPort));
            System.out.println("Connecté au serveur : " + client.getRemoteAddress());

            String username;
            StringBuilder message = new StringBuilder();

            Scanner scanner = new Scanner(System.in);

            System.out.println("Entrez le nom d'utilisateur :");

            username = scanner.nextLine();

            System.out.println("Entrez le contenu de votre message :");

            while (scanner.hasNext()) {
                message.append(scanner.nextLine()).append("\n");
            }

            String request = "RCV_IDS author:@yacine limit:1\r\n \r\n";
            //String request = "PUBLISH author:@" + username + "\r\n" + message + "\r\n";

            ByteBuffer buffer = ByteBuffer.wrap(request.getBytes());
            client.write(buffer);

            buffer = ByteBuffer.allocate(1024);
            client.read(buffer);
            buffer.flip();
            String response = new String(buffer.array()).trim();
            System.out.println(response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
