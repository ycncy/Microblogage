package request;

import util.StringSplitter;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.*;

public class Republish implements Request {

    @Override
    public void execute(Socket clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        String author = header_map.get("author");
        String msg_id = header_map.get("msg_id");

        System.out.println(author);
        System.out.println(msg_id);

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT Content FROM Messages WHERE MessageId=%s", msg_id);

            ResultSet query = connectionStatement.executeQuery(request);
            String content = query.getString("Content");

            ResultSet user = connectionStatement.executeQuery(String.format("SELECT UserId FROM Users WHERE Username='%s'", author));
            int authorId = user.getInt("UserId");

            String insert_request = "INSERT INTO Messages (Author, AuthorId, Content) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insert_request);
            preparedStatement.setString(1, author);
            preparedStatement.setInt(2, authorId);
            preparedStatement.setString(3, content);

            preparedStatement.executeUpdate();

            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            out.println("OK");
            out.flush();

            connectionStatement.close();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
