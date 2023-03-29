package client;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Publisher {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 1234);
            System.out.println("Connecté au serveur");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

            String username;
            StringBuilder message = new StringBuilder();

            Scanner scanner = new Scanner(System.in);

            System.out.println("Entrez le nom d'utilisateur :");

            username = scanner.nextLine();

            System.out.println("Entrez le contenu de votre message :");

            while (scanner.hasNext()) {
                message.append(scanner.nextLine()).append("\n");
            }

            String request = "PUBLISH author:@" + username + "\r\n" + message + "\r\n";

            out.println(request);
            out.flush();

            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
