package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.*;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("///////////////////////////////////////////\nUn nouveau client s'est connecté \n");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            OutputStream out = socket.getOutputStream();

            String[] message = in.readLine().split("\\s+");

            switch (message[0]) {
                case "PUBLISH" -> {
                    String[] author = message[1].split(":@");
                    String userMessage = in.readLine();

                    executePublish(userMessage, author[1]);
                    out.write("OK".getBytes());
                }
                case "RCV_IDS", "RCV_MSG", default -> System.out.println("ERROR");

            }

            socket.close();

            System.out.println("Connexion avec le client fermée");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
