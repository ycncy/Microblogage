package request.flux;

import request.Request;

import java.io.*;
import java.net.Socket;
import java.nio.channels.*;

public class Unsubscribe implements Request {

    @Override
    public void execute(Socket clientSocket, String header, String body) throws IOException {

    }
}
