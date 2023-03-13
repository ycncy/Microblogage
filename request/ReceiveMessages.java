package request;

import java.nio.channels.*;
import java.util.*;

public class ReceiveMessages implements Request {

    @Override
    public void execute(SocketChannel clientSocket, String header, String body) {
        String[] header_parts = header.split("\\s+");

        Map<String, String> header_map = new HashMap<>();

        for (String element : header_parts) {
            String[] element_splited = element.split(":");
            header_map.put(element_splited[0], element_splited[1]);
        }
    }
}
