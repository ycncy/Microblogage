import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private static final String connectionAddress = "localhost";
    private static final int connectionPort = 1234;

    public Client(String connectionAddress, int connectionPort) {
        //this.connectionAddress = connectionAddress;
        //this.connectionPort = connectionPort;
    }

    public static void run() throws IOException {
        Socket client = new Socket(connectionAddress, connectionPort);
        System.out.println("Connecté au serveur : " + client.getRemoteSocketAddress());

        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        String message = "Hello, serveur!";
        out.writeBytes(message);
        System.out.println("Message envoyé : " + message);

        client.close();
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
