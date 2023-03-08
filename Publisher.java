import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Publisher {

    private static final String connectionAddress = "localhost";
    private static final int connectionPort = 1234;

    public Publisher(String connectionAddress, int connectionPort) {
        //this.connectionAddress = connectionAddress;
        //this.connectionPort = connectionPort;
    }

    public static void run() throws IOException {
        Socket client = new Socket(connectionAddress, connectionPort);
        System.out.println("Connecté au serveur : " + client.getRemoteSocketAddress());

        String username = "";
        String message = "";

        Scanner scanner = new Scanner(System.in);

        System.out.println("Entrez le nom d'utilisateur :");

        username = scanner.nextLine();

        System.out.println("Entrez le contenu de votre message :");

        while (scanner.hasNext()) {
            message += scanner.nextLine() + "\n";
        }

        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        String request = "PUBLISH author:@" + username + "\r\n" + message + "\r\n";
        out.writeBytes(request);

        client.close();
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
