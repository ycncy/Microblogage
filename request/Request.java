package request;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Request {

    void execute(SocketChannel clientSocket, String header, String body) throws IOException;
}
