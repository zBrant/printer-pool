import printer.Printer;

import java.security.InvalidAlgorithmParameterException;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException {
        if (isArgsValid(args)) {
            Printer printer = new Printer("Printer1", 0.5);
            printer.run(args[0], Integer.parseInt(args[1]), 1);
        }
    }

    private static boolean isArgsValid(String[] args) throws InvalidAlgorithmParameterException {
        if (args.length > 1) return true;
        throw new InvalidAlgorithmParameterException("Invalid arguments.");
    }
}
