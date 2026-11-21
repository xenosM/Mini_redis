package miniredis.command;

import miniredis.exceptions.InvalidCommandException;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser {
    public static Command createCommand(String input) throws InvalidCommandException {
        ArrayList<String> inputArray = new ArrayList<>(Arrays.stream(input.trim().split("\\s+")).toList());
        // Commands are case-insensitive
        String command = inputArray.get(0).toLowerCase();
        String arg1 = inputArray.size() > 1 ? inputArray.get(1) : null;
        String arg2 = inputArray.size() > 2 ? inputArray.get(2) : null;

        return switch (command) {
            case "del" -> {
                checkValidInputSize(2, inputArray.size());
                yield new DelCommand(arg1);
            }
            case "set" -> {
                checkValidInputSize(3, inputArray.size());
                yield new SetCommand(arg1, arg2);
            }
            case "exists" -> {
                checkValidInputSize(2, inputArray.size());
                yield new ExistsCommand(arg1);
            }
            case "get" -> {
                checkValidInputSize(2, inputArray.size());
                yield new GetCommand(arg1);
            }
            case "expire" -> {
                checkValidInputSize(3, inputArray.size());
                long seconds;
                try {
                    seconds = Long.parseLong(arg2);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("value is not an integer or out of range");
                }
                yield new ExpireCommand(arg1, seconds);
            }
            case "ttl" -> {
                checkValidInputSize(2, inputArray.size());
                yield new TtlCommand(arg1);
            }
            default -> throw new InvalidCommandException("The command is invalid");
        };
    }

    private static void checkValidInputSize(int expectedSize, int actualSize) {
        if (actualSize < expectedSize) {
            throw new InvalidCommandException("Value cannot be null");
        } else if (actualSize > expectedSize) {
            throw new InvalidCommandException("Passed in too many arguments");
        }
    }
}
