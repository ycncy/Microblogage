package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Follower {

    public static void main(String[] args) {
        try {
            SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1234));
            System.out.println("Connecté au serveur : " + client.getRemoteAddress());

            //Demande les 5 derniers messages de "yacine"
            String request = "RCV_IDS author:yacine limit:5\r\n\r\n";

            ByteBuffer buffer = ByteBuffer.wrap(request.getBytes());
            client.write(buffer);

            //Reçoit les identifiants
            buffer = ByteBuffer.allocate(1024);
            client.read(buffer);
            buffer.flip();
            String[] response = new String(buffer.array()).trim().split("\n");

            //Demande et affiche les messages demandés
            for (int i = 1; i < response.length; i++) {
                client = SocketChannel.open(new InetSocketAddress("localhost", 1234));

                String rcv_msg_request = String.format("RCV_MSG msg_id:%s", response[1]);

                ByteBuffer messageBuffer = ByteBuffer.wrap(rcv_msg_request.getBytes());
                client.write(messageBuffer);

                messageBuffer = ByteBuffer.allocate(4096);
                client.read(messageBuffer);
                messageBuffer.flip();
                String msg_response = new String(messageBuffer.array()).trim();
                String message = msg_response.split("\r\n")[1];

                System.out.printf("Message %s : %s%n", response[i], message);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
