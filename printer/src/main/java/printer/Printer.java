package printer;

import lombok.Getter;
import lombok.Setter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.json.JSONObject;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public Printer(String name, double lossProbability) {
        this.name = name;
        this.lossProbability = lossProbability;
    }

    public void run(long timeLimit) {
        long endTime = System.currentTimeMillis() + timeLimit;
        String brokerURL = "tcp://localhost:61616";
        String queueName = "queue-1";

        Connection connection = null;
        Session session = null;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(queueName);

            MessageConsumer consumer = session.createConsumer(destination);

            Connection finalConnection = connection;
            Session finalSession = session;
            consumer.setMessageListener(message -> {
                try {
                    if (System.currentTimeMillis() > endTime) {
                        consumer.close();
                        finalSession.close();
                        finalConnection.close();
                        printerLog += generateSummary();
                        writeLogTxt(printerLog);
                        System.out.println("Time limit reached. Consumer has stopped receiving messages.");
                        System.exit(0);
                    }
                    if (message instanceof ActiveMQBytesMessage) {
                        ActiveMQBytesMessage bytesMessage = (ActiveMQBytesMessage) message;
                        byte[] byteData = new byte[(int) bytesMessage.getBodyLength()];
                        bytesMessage.readBytes(byteData);

                        String messageText = new String(byteData, StandardCharsets.UTF_8); // Convert bytes to String
                        writeLog(messageText, lossProbability);
                    }
                } catch (JMSException e) {
                    System.err.println("Error processing message: " + e.getMessage());
                }
            });
            System.out.println("Consumer waiting for messages in queue: " + queueName);

            System.in.read();

        } catch (Exception e) {
            System.err.println("Error in consumer: " + e.getMessage());
        } finally {
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Writes a log entry and updates print or loss counters
    public void writeLog(String doc, double lossProbability) {
        generateRandom = random.nextDouble();
        if (lossProbability > generateRandom) {
            lostDocuments++;  // Increment lost document counter
            printerLog += "\nDocument lost";
        } else {
            printedDocuments++;  // Increment total printed documents

            // Extract data from the JSON document
            JSONObject jsonObject = new JSONObject(doc);
            String message = jsonObject.getString("message");
            String timestamp = jsonObject.getString("timestamp");

            // Update print count for the given clientId

            // Add a log entry in the required format
            printerLog += "\nprinted the file: " + message + " at: " + timestamp;

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

    // Generates a summary of the print session
    private String generateSummary() {
        StringBuilder summary = new StringBuilder("\n\n--- Log Summary ---\n");
        summary.append("Total printed documents: ").append(printedDocuments).append("\n");
        summary.append("Total lost documents: ").append(lostDocuments).append("\n");

        return summary.toString();
    }
}
