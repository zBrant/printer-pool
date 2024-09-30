package printer;

import integration.Buffer;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Printer {
    private String name;
    private int printedDocuments = 0;
    private double lossProbability;
    private Random random = new Random();

    public Printer(String name, double lossProbability) {
        this.name = name;
        this.lossProbability = lossProbability;
    }

    public void run(String address, int port) {
        Buffer buffer = new Buffer(address, port);
        while (isBufferConnected(buffer)) {
            printDocument(buffer.fetchDocument(), lossProbability);
        }
    }

    public void printDocument(StringBuilder doc, double lossProbability) {
        if (random.nextDouble() >= lossProbability) {
            System.out.println("Document lost - " + doc);
        } else {
            System.out.println(this.name + " printed the " + doc + " at " + System.currentTimeMillis());
            this.incrementPrintedDocuments();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
