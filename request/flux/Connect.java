package request.flux;

import request.Request;
import util.StringSplitter;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.*;
import java.util.Map;

public class Connect implements Request {

    @Override
    public void execute(Socket clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            String request = String.format("SELECT * FROM Users WHERE Username='%s'", header_map.get("user"));

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(request);

            //if (resultSet.getString("Username") == null)
                //clientSocket.write(ByteBuffer.wrap("ERROR".getBytes()));

            //else {
                PreparedStatement connectionStatement = connection.prepareStatement(request);

                connectionStatement.setString(1, header_map.get("user"));

                connectionStatement.executeUpdate();

                //clientSocket.write(ByteBuffer.wrap("OK".getBytes()));
            //}

        } catch (SQLException | ClassNotFoundException e) {
            //clientSocket.write(ByteBuffer.wrap("ERROR".getBytes()));
            throw new RuntimeException(e);
        }
    }
}
