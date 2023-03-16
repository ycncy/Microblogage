package request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.*;

public class Publish implements Request {

    @Override
    public void execute(SocketChannel client, String header, String body) {
        String[] headerArray = header.split("\\s+");

        if (headerArray.length > 1) {
            try {
                client.write(ByteBuffer.wrap("La requête contient plus d'éléments que prévu".getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String author = headerArray[0].split(":@")[1];

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT * FROM Users WHERE Username = '%s'", author);

            ResultSet user = connectionStatement.executeQuery(request);

            while (user.next()) {
                String insert = "INSERT INTO Posts (UserId, Content, author) VALUES (?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insert);

                pstmt.setInt(1, user.getInt("UserId"));
                pstmt.setString(2, body);
                pstmt.setString(3, author);

                pstmt.executeUpdate();
            }

            client.write(ByteBuffer.wrap("OK".getBytes()));

            connectionStatement.close();

        } catch (ClassNotFoundException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
