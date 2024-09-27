package integration;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Setter
@Getter
public class Buffer {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Buffer(String serverAddress, int port) {
        this.socket = getSocket(serverAddress, port);
        this.in = getIn();
        this.out = getOut();
    }

    public StringBuilder fetchDocument() {
        try {
            // Send request
            out.println("GET /json");

            // Receive document
            StringBuilder document = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }

            return document;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Socket getSocket(String serverAddress, int port) {
        try {
            return new Socket(serverAddress, port);
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to the buffer!");
        }
    }

    private PrintWriter getOut() {
        try {
            return new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException("Unable to establish connection with the out stream!");
        }
    }

    private BufferedReader getIn() {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to establish connection with the in stream!");
        }
    }
}