package client;

import request.Requests;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 1234);

        Scanner scanner = new Scanner(System.in);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        while (true) {
            System.out.println("Entrez votre nom d'utilisateur pour vous connecter");

            String user = scanner.nextLine();

            out.write("CONNECT user:@" + user + "\r\n \r\n");
            out.flush();

            String response = in.readLine();
            System.out.println(response);

            if (response.equals("OK")) {
                while (true) {

                    System.out.println("\n\n 1) Envoyer une requête\n 2) Quitter");

                    String string = scanner.nextLine();

                    if (string.equals("2")) {
                        break;
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

                        System.out.println(in.lines().toArray());
                        System.out.println(in.readLine());

                        /*StringBuilder sb = new StringBuilder();
                        for (String line : in.lines().toList()) {
                            sb.append(line);
                        }
                        System.out.println(sb);*/

                        /*
                        while ((line = in.readLine()) != null) sb.append(line);
                        System.out.println(sb);*/
                    }
                }
            }
        }
    }
}
