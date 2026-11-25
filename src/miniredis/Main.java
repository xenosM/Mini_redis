package miniredis;

import miniredis.command.Command;
import miniredis.command.CommandParser;
import miniredis.store.ExpiryCleaner;
import miniredis.store.InMemoryStore;

import java.util.Scanner;

public class Main {
    final static long CLEANER_INTERVAL = 1000;

    public static void main(String[] args) throws InterruptedException {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        try (ExpiryCleaner cleaner = new ExpiryCleaner(store, CLEANER_INTERVAL); Scanner s = new Scanner(System.in);) {
            cleaner.start();
            while (true) {
                Command command;
                String input, output;

                System.out.print("Mini-Redis> ");
                if (!s.hasNextLine()) break;
                input = s.nextLine();
                if (input.equalsIgnoreCase("q")) break;
                if (input.isBlank()) continue;
                try {
                    command = CommandParser.createCommand(input);
                    output = command.execute(store);
                } catch (RuntimeException e) {
                    String msg = e.getMessage();
                    if ((msg) != null) {
                        System.out.println("ERROR: " + msg);
                    } else {
                        System.out.println(e.getClass().getSimpleName() + " Error Detected");
                    }
                    continue;
                }
                System.out.println(output);
            }
            System.out.println("\n Quitting Redis....");
        }

    }
}
