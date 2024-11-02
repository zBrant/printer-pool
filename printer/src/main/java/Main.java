import printer.Printer;

import java.security.InvalidAlgorithmParameterException;

public class Main {
    public static void main(String[] args) {
        Printer printer = new Printer("Printer1", 0.5);
        printer.run(30000); // Set time limit, e.g., 60000 ms = 1 minute
    }

}
