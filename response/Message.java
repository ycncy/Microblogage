package response;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;

public class Message {

    public void execute(Socket client, String condition) throws IOException {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT * FROM Messages WHERE %s", condition);

            ResultSet result = connectionStatement.executeQuery(request);

            String author = result.getString("Author");
            int message_id = result.getInt("MessageId");
            String content = result.getString("Content");

            String header = String.format("MSG author:@%s msg_id:%d", author, message_id);

            String response = String.format("%s\r\n%s\r\n", header, content);

            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

            System.out.println(response);

            out.println(response);
            out.flush();

            connectionStatement.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
