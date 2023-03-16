package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Repost {

    public static void main(String[] args) {
        String[] users = {"matteo", "yacine"};

        try {
            List<String> ids = new ArrayList<>();

            for (String user : users) {
                SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1234));
                System.out.println("Connecté au serveur : " + client.getRemoteAddress());

                String request = String.format("RCV_IDS author:@%s", user);

                ByteBuffer buffer = ByteBuffer.wrap(request.getBytes());
                client.write(buffer);

                buffer = ByteBuffer.allocate(1024);
                client.read(buffer);
                buffer.flip();
                String response = new String(buffer.array()).trim();

                String[] rcv_ids = response.split("\n");

                for (int i = 1; i < rcv_ids.length; i++) ids.add(rcv_ids[i]);
            }

            for (String id : ids) {
                SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1234));
                System.out.println("Connecté au serveur : " + client.getRemoteAddress());

                String request = String.format("REPUBLISH author:@yacine msg_id:%s", id);

                ByteBuffer buffer = ByteBuffer.wrap(request.getBytes());
                client.write(buffer);

                buffer = ByteBuffer.allocate(1024);
                client.read(buffer);
                buffer.flip();
                String response = new String(buffer.array()).trim();

                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
