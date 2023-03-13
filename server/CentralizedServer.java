package server;

import request.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public class CentralizedServer {

    private static final Publish publish = new Publish();
    private static final ReceiveMessages receiveMessages = new ReceiveMessages();

    private final static int port = 1234;

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);

        Selector selector = Selector.open();

        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();

                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    StringBuilder sb = new StringBuilder();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    while (client.read(buffer) > 0) {
                        buffer.flip();
                        sb.append(Charset.defaultCharset().decode(buffer));
                        buffer.clear();
                    }

                    String message = sb.toString();

                    buffer.clear();

                    String[] parts = message.split("\r\n");

                    String pattern = "^(\\w+)\\s(.+)$";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(parts[0]);

                    String request = "";
                    String header = "";
                    String body = parts[1];

                    if (m.find()) {
                        request = m.group(1);
                        header = m.group(2);
                    }

                    switch (request) {

                        case "PUBLISH":
                            publish.execute(client, header, body);

                        case "RCV_IDS":
                            receiveMessages.execute(client, header, body);
                    }

                    client.close();
                }
            }
        }
    }
}