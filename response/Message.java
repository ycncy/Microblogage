package response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.*;

public class Message {

    public void execute(SocketChannel client, String condition) throws IOException {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT * FROM Posts WHERE %s", condition);

            ResultSet result = connectionStatement.executeQuery(request);

            String author = result.getString("author");
            int message_id = result.getInt("MessageId");
            String content = result.getString("content");

            String header = String.format("MSG author:@%s msg_id:%d", author, message_id);

            String response = String.format("%s\r\n%s\r\n", header, content);

            ByteBuffer data = ByteBuffer.wrap(response.getBytes());

            client.write(data);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
