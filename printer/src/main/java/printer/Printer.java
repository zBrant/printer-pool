package printer;

import integration.Buffer;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Printer {
    private String name;
    private int printedDocuments = 0;
    private double lossProbability;
    String printerLog = "";
    Random random = new Random();
    Double generateRandom;

    // Executor service for scheduling tasks
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Printer(String name, double lossProbability) {
        this.name = name;
        this.lossProbability = lossProbability;
    }

    public void run(String address, int port, double intervalMinutes) {
        Buffer buffer = new Buffer(address, port);
        double startTime = System.currentTimeMillis();
        double duration = intervalMinutes * 60 * 1000;

        while (isBufferConnected(buffer) && System.currentTimeMillis() - startTime < duration) {
            String document = String.valueOf(buffer.fetchDocument());
            writeLog(document, lossProbability);
        }
        writeLogTxt(printerLog);
    }

    public void writeLog(String doc, double lossProbability) {
        Date resultDate = new Date(System.currentTimeMillis());
        generateRandom = random.nextDouble();
        if (lossProbability > generateRandom) {
            printerLog += "\nDocument lost";
        } else {
            this.incrementPrintedDocuments();
            printerLog += "\n" + this.name + " printed the " + doc + " at " + resultDate + "\nPrinted documents: " + this.printedDocuments;
            try {
                TimeUnit.MILLISECONDS.sleep(100);  // Simulating print delay
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void writeLogTxt(String document) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("printer/src/main/java/printer/print_log.txt", false))) {
            writer.write(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isBufferConnected(Buffer buffer) {
        try {
            return !buffer.getSocket().getKeepAlive();
        } catch (SocketException e) {
            throw new RuntimeException("Invalid socket.");
        }
    }

    public void incrementPrintedDocuments() {
        printedDocuments++;
    }
}
