package request;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public interface Request {

    void execute(Socket clientSocket, String header, String body) throws IOException;
}
