package request;

import response.MessageIds;

import java.io.IOException;
import java.nio.channels.*;
import java.sql.*;
import java.util.*;

public class ReceiveMessages implements Request {

    private final MessageIds messageIds = new MessageIds();

    @Override
    public void execute(SocketChannel clientSocket, String header, String body) throws IOException {
        String[] header_parts = header.split("\\s+");

        Map<String, String> header_map = new HashMap<>();

        for (String element : header_parts) {
            String[] element_splited = element.split(":@|:");
            header_map.put(element_splited[0], element_splited[1]);
        }

        StringBuilder select_condition = new StringBuilder();

        if (header_map.containsKey("author") && header_map.containsKey("tag"))
            select_condition.append(String.format("author='%s' AND tag='%s'", header_map.get("author"), header_map.get("tag")));
        else if (header_map.containsKey("tag"))
            select_condition.append(String.format("tag='%s'", header_map.get("tag")));
        else if (header_map.containsKey("author"))
            select_condition.append(String.format("author='%s'", header_map.get("author")));

        if (header_map.containsKey("since_id")) {
            if (!select_condition.toString().isEmpty()) select_condition.append(" AND ");

            else select_condition.append(" WHERE ");

            select_condition.append(String.format("MessageId >= %d", Integer.parseInt(header_map.get("since_id"))));
        }

        select_condition.append("\nORDER BY MessageId DESC");

        if (header_map.containsKey("limit")) {
            select_condition.append(String.format("\nLIMIT %d", Integer.parseInt(header_map.get("limit"))));
        }

        messageIds.execute(clientSocket, "MSG_IDS", select(select_condition.toString()));
    }

    private String select (String condition) {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/db.sqlite");

            Statement connectionStatement = connection.createStatement();

            String request = String.format("SELECT MessageId FROM Posts WHERE %s", condition);

            ResultSet result = connectionStatement.executeQuery(request);

            String corps = "";

            while (result.next()) {
                corps += result.getInt("MessageId") + "\n";
            }

            return corps;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
