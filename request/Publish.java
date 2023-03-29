package request;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;

public class Publish implements Request {

    @Override
    public void execute(Socket client, String header, String body) throws IOException {
        String[] headerArray = header.split("\\s+");

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        if (headerArray.length > 1) {
            out.write("La requête contient plus d'éléments que prévu");
        }

        String author = headerArray[0].split(":@")[1];

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT * FROM Users WHERE Username = '%s'", author);

            ResultSet user = connectionStatement.executeQuery(request);

            while (user.next()) {
                String insert = "INSERT INTO Messages (AuthorId, Content, Author) VALUES (?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insert);

                pstmt.setInt(1, user.getInt("UserId"));
                pstmt.setString(2, body);
                pstmt.setString(3, author);

                pstmt.executeUpdate();
            }

            out.println("OK");
            out.flush();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
