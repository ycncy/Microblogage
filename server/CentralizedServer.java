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
    private static final ReceiveIDS receiveIDS = new ReceiveIDS();
    private static final ReceiveMessages receiveMessages = new ReceiveMessages();
    private static final Reply reply = new Reply();
    private static final Republish republish = new Republish();

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

                    ByteBuffer buffer = ByteBuffer.allocate(4096);

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
                    String body = parts.length > 1 ? parts[1] : "";

                    if (m.find()) {
                        request = m.group(1);
                        header = m.group(2);
                    }

                    if (request.equals("PUBLISH"))
                        publish.execute(client, header, body);

                    if (request.equals("RCV_IDS"))
                        receiveIDS.execute(client, header, body);

                    if (request.equals("RCV_MSG"))
                        receiveMessages.execute(client, header, body);

                    if (request.equals("REPLY"))
                        reply.execute(client, header, body);

                    if (request.equals("REPUBLISH"))
                        republish.execute(client, header, body);

                    client.close();
                }
            }
        }
    }
}