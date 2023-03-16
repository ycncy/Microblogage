package response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface MessageResponse {

    void execute (SocketChannel client, String condition) throws IOException;
}
