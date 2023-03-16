package request;

import response.Message;
import util.StringSplitter;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class ReceiveMessages implements Request {

    private final Message messageResponse = new Message();

    @Override
    public void execute(SocketChannel clientSocket, String header, String body) throws IOException {
        Map<String, String> header_map = StringSplitter.split(header);

        String select_condition = String.format("MessageId=%s", header_map.get("msg_id"));

        messageResponse.execute(clientSocket, select_condition);
    }
}
