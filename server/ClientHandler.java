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

    public static Map<String, Socket> clients = new HashMap<>();
    public static Map<String, ConcurrentLinkedQueue<String>> queues = new HashMap<>();
    public static Map<String, List<String>> followers = new HashMap<>();

    public void run() {
        try {
            String username = null;
            while (true) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                String first_header = in.readLine();
                String first_body = in.readLine();

                String message = first_header + "\r\n" + first_body + "\r\n";

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
                        //System.out.println(queues);
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
                        String user = header_map.get("user");
                        if (user != null) {
                            username = user;
                            clients.put(username, client);
                            out.println("OK");
                            out.flush();
                        } else {
                            out.println("ERROR");
                            out.flush();
                        }
                        break;
                    case "SUBSCRIBE":
                        // Subscribe the client to messages from the specified author or tag
                        String author = header_map.get("author");
                        String tag = header_map.get("tag");
                        if (author != null) {
                            if (!followers.containsKey(author)) {
                                queues.put(username, new ConcurrentLinkedQueue<>());
                                followers.put(author, new ArrayList<>());
                            }
                            if (!followers.get(author).contains(username)) {
                                followers.get(author).add(username);
                            }
                            out.println("OK");
                            out.flush();
                        } else if (tag != null) {
                            if (!queues.containsKey(tag)) {
                                queues.put(tag, new ConcurrentLinkedQueue<>());
                            }
                            if (!followers.containsKey(tag)) {
                                followers.put(tag, new ArrayList<>());
                            }
                            if (!followers.get(tag).contains(username)) {
                                followers.get(tag).add(username);
                            }
                            out.println("OK");
                            out.flush();
                        } else {
                            out.println("ERROR");
                            out.flush();
                        }
                        System.out.println(followers);
                        break;
                    case "UNSUBSCRIBE":
                        String unsubscribeAuthor = header_map.get("author");
                        String unsubscribeTag = header_map.get("tag");
                        if (unsubscribeAuthor != null) {
                            if (followers.containsKey(unsubscribeAuthor) && followers.get(unsubscribeAuthor).contains(username)) {
                                followers.get(unsubscribeAuthor).remove(username);
                                out.println("OK");
                                out.flush();
                            } else {
                                out.println("ERROR");
                                out.flush();
                            }
                        } else if (unsubscribeTag != null) {
                            if (followers.containsKey(unsubscribeTag) && followers.get(unsubscribeTag).contains(username)) {
                                followers.get(unsubscribeTag).remove(username);
                                out.println("OK");
                                out.flush();
                            } else {
                                out.println("ERROR");
                                out.flush();
                            }
                        } else {
                            out.println("ERROR");
                            out.flush();
                        }
                        break;
                    default:
                        System.out.println("Unknown command: " + request);
                        break;
                }

                for (String user : queues.keySet()) {
                    while (!queues.get(user).isEmpty()) {
                        String user_message = queues.get(user).poll();

                        PrintWriter user_out = new PrintWriter(new OutputStreamWriter(clients.get(user).getOutputStream()));

                        assert user_message != null;
                        user_out.println(user_message);
                        user_out.flush();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
