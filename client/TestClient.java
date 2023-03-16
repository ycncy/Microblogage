package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestClient {

    public static void main(String[] args) {
        try {
            SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1234));
            System.out.println("Connecté au serveur : " + client.getRemoteAddress());

            String request = "RCV_MSG msg_id:5\r\n\r\n";

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
