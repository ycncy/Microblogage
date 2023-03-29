package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Follower {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 1234);
            System.out.println("Connecté au serveur");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

            //Demande les 5 derniers messages de "yacine"
            String request = "RCV_IDS author:@yacine limit:5\r\n\r\n";

            out.println(request);
            out.flush();


            //Reçoit les identifiants
            String line;
            List<String> response = new ArrayList<>();
            while (!(line = in.readLine()).equals("")) {
                response.add(line);
            }

            //Demande et affiche les messages demandés
            for (int i = 1; i < response.size(); i++) {
                String rcv_msg_request = "RCV_MSG msg_id:" + response.get(i);

                out.println(rcv_msg_request);
                out.flush();

                String tmp;
                while ((tmp = in.readLine()) != null) {
                    System.out.println(tmp);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
