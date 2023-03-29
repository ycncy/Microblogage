package client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Repost {

    public static void main(String[] args) {
        String[] users = {"yacine", "matteo"};

        try {
            List<String> ids = new ArrayList<>();

            Socket client = new Socket("localhost", 1234);
            System.out.println("Connecté au serveur");

            for (String user : users) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                String request = String.format("RCV_IDS author:@%s\r\n\r\n", user);

                out.println(request);
                out.flush();

                String line;
                List<String> new_ids = new ArrayList<>();
                while (!(line = in.readLine()).equals("")) {
                    new_ids.add(line);
                }

                for (int i = 1; i < new_ids.size(); i++) ids.add(new_ids.get(i));
            }

            for (String id : ids) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                String request = "REPUBLISH author:@yacine msg_id:" + id + "\r\n\r\n";

                out.println(request);
                out.flush();

                String line = in.readLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
