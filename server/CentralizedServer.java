package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CentralizedServer {

    private final static int port = 1234;

    public CentralizedServer(int port) {
        //this.port = port;
    }

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

                        case "PUBLISH":
                            String[] headerArray = header.split("\\s+");

                            if (headerArray.length > 1) {
                                client.write(ByteBuffer.wrap("La requête contient plus d'éléments que prévu".getBytes()));
                                break;
                            }

                            String author = headerArray[0].split(":@")[1];

                            executePublish(body, author);


                        default:
                            System.out.println("ERROR");
                    }

                    buffer.clear();
                    client.close();
                }
            }
        }
    }


    private static void executePublish(String message, String author) {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT * FROM Users WHERE Username = '%s'", author);

            ResultSet user = connectionStatement.executeQuery(request);

            while (user.next()) {
                String insert = "INSERT INTO Posts (UserId, Content) VALUES (?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insert);

                pstmt.setInt(1, user.getInt("UserId"));
                pstmt.setString(2, message);

                pstmt.executeUpdate();
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}