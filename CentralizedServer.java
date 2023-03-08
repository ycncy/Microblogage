import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

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

                System.out.println("///////////////////////////////////////////\nUn nouveau client s'est connecté \n///////////////////////////////////////////");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message = "";
                int id = System.identityHashCode(in);

                if (in.readLine().startsWith("PUBLISH")) {
                    message = in.readLine();
                }

                System.out.println("Message " +  id + " : " + message);

                clientSocket.close();

                System.out.println("Connexion avec le client fermée");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
