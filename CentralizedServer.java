import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class CentralizedServer {

    private final static int port = 1234;

    public CentralizedServer(int port) {
        //this.port = port;
    }

    public static void run() throws IOException {

        //ENTETE : PUBLISH author:@user \r\n corps \r\n

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("///////////////////////////////////////////\nUn nouveau client s'est connecté \n");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                OutputStream out = clientSocket.getOutputStream();

                String[] message = in.readLine().split("\\s+");

                if (message[0].equals("PUBLISH")) {

                }

                switch (message[0]) {
                    case "PUBLISH":
                        String[] author = message[1].split(":@");
                        String userMessage = in.readLine();
                        executePublish(userMessage, author[1]);

                        out.write("OK".getBytes());
                        break;

                    case "RCV_IDS":


                    case "RCV_MSG":

                    default:
                        System.out.println("ERROR");
                }

                clientSocket.close();

                System.out.println("Connexion avec le client fermée");
            }
        }
    }

    private static void executePublish(String message, String author) {
        System.out.println("Message de " + author + " : " + message);
    }

    public static void main(String[] args) throws ClassNotFoundException {

    }

}
