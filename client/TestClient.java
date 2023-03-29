package client;

import request.Requests;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TestClient {

    /*public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 1234);

        Scanner scanner = new Scanner(System.in);

        loop:
        while (true) {
            System.out.println("Entrez votre nom d'utilisateur pour vous connecter");

            String user = scanner.nextLine();

            PrintWriter out = new PrintWriter(client.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.write("CONNECT user:@" + user + "\r\n \r\n");
            out.flush();

            String response = in.readLine();
            System.out.println(response);

            if (response.equals("OK")) {

                Thread thread = new Thread(() -> {
                    try {
                        while (true) {
                            String responseLine = readResponse(in);
                            System.out.println(responseLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                thread.start();

                while (true) {

                    System.out.println("\n\n 1) Envoyer une requête\n 2) Quitter");

                    String string = scanner.nextLine();

                    if (string.equals("2")) {
                        break loop;
                    }

                    if (string.equals("1")) {

                        System.out.println("Entrez la categorie de votre requête : ");

                        String request = scanner.nextLine();

                        System.out.println("Entrez l'entête de votre requête : ");

                        String header = scanner.nextLine();

                        System.out.println("Entrez le corps de votre requête (Appuyez sur entrée s'il est vide) : ");

                        String body = scanner.nextLine();

                        String final_request = String.format("%s %s\r\n%s\r\n", request, header, body);

                        out.println(final_request);
                        out.flush();

                        System.out.println("Reçu");
                    }
                }
            }
        }
    }

    private static String readResponse(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            sb.append(buffer, 0, read);
            if (sb.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }
        return sb.toString();
    }*/

    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 1234;

        try (Socket socket = new Socket(hostName, portNumber)) {

            System.out.print("Username: ");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.nextLine();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Connect to the server
            out.println("CONNECT user:@" + username);
            out.println();
            out.flush();
            String response = in.readLine();
            if (response.equals("OK")) {
                System.out.println("Connected to server.");
            } else {
                System.out.println("Error connecting to server.");
                socket.close();
                return;
            }

            // Publish a message
            out.println("PUBLISH author:@ycn\r\nThis is a message.\r\n");
            String publishResponse = in.readLine();
            System.out.println(publishResponse);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
