package request;

import response.MessageIds;
import util.StringSplitter;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;

public class ReceiveIDS implements Request {

    private final MessageIds messageIds = new MessageIds();

    @Override
    public void execute(SocketChannel clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        StringBuilder select_condition = new StringBuilder();

        if (header_map.containsKey("author") && header_map.containsKey("tag"))
            select_condition.append(String.format("Author='%s' AND Tag='%s'", header_map.get("author"), header_map.get("tag")));
        else if (header_map.containsKey("tag"))
            select_condition.append(String.format("Tag='%s'", header_map.get("tag")));
        else if (header_map.containsKey("author"))
            select_condition.append(String.format("Author='%s'", header_map.get("author")));

        if (header_map.containsKey("since_id")) {
            if (!select_condition.toString().isEmpty()) select_condition.append(" AND ");

            else select_condition.append(" WHERE ");

            select_condition.append(String.format("MessageId >= %d", Integer.parseInt(header_map.get("since_id"))));
        }

        select_condition.append("\nORDER BY MessageId DESC");

        if (header_map.containsKey("limit")) {
            select_condition.append(String.format("\nLIMIT %d", Integer.parseInt(header_map.get("limit"))));
        }

        messageIds.execute(clientSocket, select_condition.toString());
    }
}
