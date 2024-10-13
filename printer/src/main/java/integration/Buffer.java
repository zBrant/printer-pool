package integration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Setter
@Getter
public class Buffer {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress;
    private int port;
    JSONObject json = createJson();

    public Buffer(String serverAddress, int port) {
        this.socket = createSocket(serverAddress, port);
        this.serverAddress = serverAddress;
        this.port = port;
        this.in = getIn();
        this.out = getOut();
    }

    public String fetchDocument() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create the JSON and send the request
            json = createJson();
            out.println(json);

            // Read the server's response
            StringBuilder jsonResponse = receiveMessage(socket);
            if (!JsonParser.parseString(String.valueOf(jsonResponse)).isJsonObject()) return "";

            // Convert JSON to a Java object (using Gson)
            JsonObject jsonObject = JsonParser.parseString(String.valueOf(jsonResponse)).getAsJsonObject();

            return jsonObject.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject createJson() {
        JSONObject json = new JSONObject();
        json.put("message", "get file");
        return json;
    }

    private static Socket createSocket(String serverAddress, int port) {
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
            throw new RuntimeException("Unable to establish connection with the output stream!");
        }
    }

    private BufferedReader getIn() {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to establish connection with the input stream!");
        }
    }

    private static StringBuilder receiveMessage(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        // Read lines of the response
        line = in.readLine();
        response.append(line);

        return response;
    }
}