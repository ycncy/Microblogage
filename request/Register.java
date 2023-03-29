package request;

import util.StringSplitter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.*;
import java.util.Map;

public class Register implements Request {

    @Override
    public void execute(Socket clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            String request = "INSERT INTO Users (Username) VALUES (?)";

            PreparedStatement connectionStatement = connection.prepareStatement(request);

            connectionStatement.setString(1, header_map.get("user"));

            connectionStatement.executeUpdate();

            out.write("OK");
            out.flush();

        } catch (SQLException | ClassNotFoundException e) {

            out.write("ERROR");
            out.flush();
            throw new RuntimeException(e);
        }
    }
}
