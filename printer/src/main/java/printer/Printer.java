package printer;

import integration.Buffer;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Printer {
    private String name;
    private int printedDocuments = 0;  // Total printed documents
    private int lostDocuments = 0;  // Counter for lost documents
    private double lossProbability;
    String printerLog = "";  // Log of print events
    Random random = new Random();
    Double generateRandom;
    private HashMap<Integer, Integer> clientPrintCount = new HashMap<>();  // Map of print counts per client

    // Executor service for scheduling tasks
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Printer(String name, double lossProbability) {
        this.name = name;
        this.lossProbability = lossProbability;
    }

    // Main execution loop that processes documents from the buffer
    public void run(String address, int port, double intervalMinutes) {
        Buffer buffer = new Buffer(address, port);
        double startTime = System.currentTimeMillis();
        double duration = intervalMinutes * 60 * 1000;

        while (isBufferConnected(buffer) && System.currentTimeMillis() - startTime < duration) {
            String document = String.valueOf(buffer.fetchDocument());
            writeLog(document, lossProbability);
        }
        printerLog += generateSummary();
        writeLogTxt(printerLog);
    }

    // Writes a log entry and updates print or loss counters
    public void writeLog(String doc, double lossProbability) {
        Date resultDate = new Date(System.currentTimeMillis());
        generateRandom = random.nextDouble();
        if (lossProbability > generateRandom) {
            lostDocuments++;  // Increment lost document counter
            printerLog += "\nDocument lost";
        } else {
            printedDocuments++;  // Increment total printed documents

            // Extract data from the JSON document
            JSONObject jsonObject = new JSONObject(doc);
            int clientId = jsonObject.getInt("clientId");
            String message = jsonObject.getString("message");
            String timestamp = jsonObject.getString("timestamp");

            // Update print count for the given clientId
            clientPrintCount.put(clientId, clientPrintCount.getOrDefault(clientId, 0) + 1);

            // Add a log entry in the required format
            printerLog += String.format(
                    "\n%s printed the process %d, with message: %s, at %s",
                    this.name, clientId, message, timestamp
            );

            // Simulate print delay
            try {
                TimeUnit.MILLISECONDS.sleep(100);  // Simulate printing time
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Writes the entire log to a text file
    private static void writeLogTxt(String document) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("printer/src/main/java/printer/print_log.txt", false))) {
            writer.write(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Checks if the buffer is still connected
    private static boolean isBufferConnected(Buffer buffer) {
        return buffer.getSocket().isConnected();
    }

    // Generates a summary of the print session
    private String generateSummary() {
        StringBuilder summary = new StringBuilder("\n\n--- Log Summary ---\n");
        summary.append("Total printed documents: ").append(printedDocuments).append("\n");
        summary.append("Total lost documents: ").append(lostDocuments).append("\n");
        summary.append("Prints per client:\n");

        clientPrintCount.forEach((clientId, count) ->
                summary.append("Client ").append(clientId).append(": ").append(count).append(" prints\n")
        );

        return summary.toString();
    }
}
