package server;

import request.*;
import util.StringSplitter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ClientHandler implements Runnable {

    private static final Publish publish = new Publish();
    private static final ReceiveIDS receiveIDS = new ReceiveIDS();
    private static final ReceiveMessages receiveMessages = new ReceiveMessages();
    private static final Reply reply = new Reply();
    private static final Republish republish = new Republish();
    private static final Register register = new Register();


    private final Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    private Map<String, Socket> clients = new HashMap<>();
    private Map<String, ConcurrentLinkedQueue<String>> queues = new HashMap<>();
    private Map<String, List<String>> followers = new HashMap<>();

    public void run() {
        try {
            String username = null;
            while (true) {
                System.out.println(clients);

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                String first_header = in.readLine();
                String first_body = in.readLine();

                String message = first_header + "\r\n" + first_body + "\r\n";

                System.out.println(message);

                String[] parts = message.split("\r\n");

                String pattern = "^(\\w+)\\s(.+)$";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(parts[0]);

                String request = "";
                String header = "";
                String body = parts.length > 1 ? parts[1] : "";

                if (m.find()) {
                    request = m.group(1);
                    header = m.group(2);
                }

                Map<String, String> header_map = StringSplitter.split(header);

                switch (request) {
                    case "PUBLISH":
                        publish.execute(client, header, body);
                        break;
                    case "RCV_IDS":
                        receiveIDS.execute(client, header, body);
                        break;
                    case "RCV_MSG":
                        receiveMessages.execute(client, header, body);
                        break;
                    case "REPLY":
                        reply.execute(client, header, body);
                        break;
                    case "REPUBLISH":
                        republish.execute(client, header, body);
                        break;
                    case "REGISTER":
                        register.execute(client, header, body);
                        break;
                    case "CONNECT":
                        clients.put(header_map.get("user"), client);
                        followers.put(header_map.get("user"), new ArrayList<>());
                        username = header_map.get("user");

                        out.println("OK");
                        out.flush();
                        break;
                    case "SUBSCRIBE":

                        out.println("OK");
                        out.flush();
                        break;
                    default:
                        System.out.println("Unknown command: " + request);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
