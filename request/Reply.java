package request;

import util.StringSplitter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.*;
import java.util.Map;

public class Reply implements Request {

    @Override
    public void execute(SocketChannel clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        String author = header_map.get("author");
        String reply_id = header_map.get("reply_to_id");

        String request = String.format("INSERT INTO Messages (Author, Content, Reply_to_id, AuthorId) VALUES (?, ?, ?, ?)");

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery(String.format("SELECT * FROM Users WHERE Username= '%s'", author));

            int authorId = resultSet.getInt("UserId");

            PreparedStatement pstmt = connection.prepareStatement(request);

            pstmt.setString(1, author);
            pstmt.setString(2, body);
            pstmt.setString(3, reply_id);
            pstmt.setInt(4, authorId);

            pstmt.executeUpdate();

            clientSocket.write(ByteBuffer.wrap("OK".getBytes()));

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
