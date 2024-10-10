package integration;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

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
    private String serverAddress;
    private int port;
    JSONObject json = createJson();;
    String line = "";

    public Buffer(String serverAddress, int port) {
        this.socket = getSocket(serverAddress, port);
        this.serverAddress = serverAddress;
        this.port = port;
        this.in = getIn();
        this.out = getOut();
    }

    public String fetchDocument() {
        try {
            // Abrir um novo socket a cada requisição
            Socket socket = getSocket(serverAddress, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Criar o JSON e enviar a requisição
            json = createJson();
            out.println(json);

            // Receber o documento
            StringBuilder document = new StringBuilder();
            line = "";
            while ((line = in.readLine()) != null) {
                document.append(line);
            }

            // Fechar o socket e streams
            in.close();
            out.close();
            socket.close();

            return document.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject createJson() {
        JSONObject json = new JSONObject();
        json.put("message", "get file");
        return json;
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