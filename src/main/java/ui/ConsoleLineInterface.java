package ui;

import util.Errorlog;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleLineInterface {
    private static final Scanner scanner = new Scanner(System.in);

    public static String consoleListener() {
        try {
            if (!scanner.hasNextLine()) {
                scanner.close();
                Errorlog.endProgramm();
            }
            return scanner.nextLine().trim();
        } catch (IllegalStateException eIS) {
            Errorlog.addError(eIS);
            UserInterface.out(new Message("Scanner ist geschlossen." + eIS.getMessage()));
            scanner.close();
            Errorlog.endProgramm();
            return null;
        } catch (NoSuchElementException eNSE) {
            Errorlog.addError(eNSE);
            scanner.close();
            UserInterface.out(new Message("Eingabe nicht verfügbar." + eNSE.getMessage()));
            Errorlog.endProgramm();
            return null;
        }
    }

    public static void print(Message m) {
        if (null != m.getContent()) {
            System.out.println(m.getContent());
        }
        if (null != m.getContents()) {
            for (String message : m.getContents()) {
                System.out.println(message);
            }
        }
    }
}
