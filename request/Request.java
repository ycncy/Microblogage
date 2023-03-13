package request;

import java.nio.channels.SocketChannel;

public interface Request {

    void execute(SocketChannel clientSocket, String header, String body);
}
