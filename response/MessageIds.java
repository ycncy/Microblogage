package response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageIds implements Response {

    @Override
    public void execute(SocketChannel client, String header, String corps) throws IOException {
        client.write(ByteBuffer.wrap((header + "\r\n" + corps).getBytes()));
    }
}
