import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class CentralizedServer {

    private final static int port = 1234;

    public CentralizedServer(int port) {
        //this.port = port;
    }

    public static void run() throws IOException, RuntimeException {

        //ENTETE : PUBLISH author:@user \r\n corps \r\n

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("///////////////////////////////////////////\nUn nouveau client s'est connecté \n");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                OutputStream out = clientSocket.getOutputStream();

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

                clientSocket.close();

                System.out.println("Connexion avec le client fermée");
            }
        }
    }

    private static void executePublish(String message, String author) {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\talha\\Desktop\\Dev\\Java\\Microblogage\\db.sqlite");

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

    public static void main(String[] args) throws IOException {
        run();
    }

}
