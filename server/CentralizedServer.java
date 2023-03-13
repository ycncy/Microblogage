package server;

import request.Publish;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class CentralizedServer {

    private static Publish publish = new Publish();

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

                    ByteBuffer buffer = ByteBuffer.allocate(20000);

                    StringBuilder data = new StringBuilder();

                    while (client.read(buffer) > 0) {
                        buffer.flip();
                        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);
                        data.append(charBuffer);
                        buffer.clear();
                    }

                    String message = data.toString();

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

                        case "PUBLISH": publish.execute(client, header, body);
                        case "RCV_MSG": publish.execute(client, header, body);

                        default:
                            System.out.println("ERROR");
                    }

                    buffer.clear();
                    client.close();
                }
            }
        }
    }
}