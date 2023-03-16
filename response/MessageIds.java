package response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.*;

public class MessageIds {

    public void execute(SocketChannel client, String condition) throws IOException {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT MessageId FROM Messages WHERE %s", condition);

            ResultSet result = connectionStatement.executeQuery(request);

            String corps = "";

            while (result.next()) {
                corps += result.getInt("MessageId") + "\n";
            }

            client.write(ByteBuffer.wrap(("MSG_IDS\r\n" + corps).getBytes()));

            connectionStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
