package response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Response {

    void execute (SocketChannel client, String header, String corps) throws IOException;
}
