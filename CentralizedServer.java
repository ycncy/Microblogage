import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralizedServer {

    private final static int port = 1234;

    public CentralizedServer(int port) {
        //this.port = port;
    }

    public static void run() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Un nouveau client s'est connecté \n///////////////////////////////////////////");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                System.out.println("Message reçu par le client :");

                System.out.println(in.readLine() + "\n///////////////////////////////////////////");

                clientSocket.close();

                System.out.println("Connexion avec le client fermée");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
