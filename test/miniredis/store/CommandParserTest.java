package miniredis.store;

import miniredis.command.*;
import miniredis.exceptions.InvalidCommandException;
import miniredis.testing.Assert;
import miniredis.testing.Test;

public class CommandParserTest {
    Store<String, String> store = new InMemoryStore<>();

    @Test
    void parsesDelCommand() {
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("DEL A"));
    }

    @Test
    void parsesSetCommand() {
        Assert.assertEquals(new SetCommand("A", "123"), CommandParser.createCommand("SET A 123"));
    }

    @Test
    void parsesExistsCommand() {
        Assert.assertEquals(new ExistsCommand("A"), CommandParser.createCommand("EXISTS A"));
    }

    @Test
    void parsesGetCommand() {
        Assert.assertEquals(new GetCommand("A"), CommandParser.createCommand("GET A"));
    }

    @Test
    void parsesExpireCommand() {
        Assert.assertEquals(new ExpireCommand("A", 123), CommandParser.createCommand("EXPIRE A 123"));
    }

    @Test
    void parsesTtlCommand() {
        Assert.assertEquals(new TtlCommand("A"), CommandParser.createCommand("TTL A"));
    }

    @Test
    void isCaseInsensitive() {
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("del A"));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("Del A"));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("dEl A"));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("deL A"));
    }


    @Test
    void rejectsZeroInputSize() {
        try {
            CommandParser.createCommand("");
            Assert.fail("Accepted null input");
        } catch (InvalidCommandException ignored) {
        }
    }

    @Test
    void rejectsExcessInputSize() {
        try {
            CommandParser.createCommand("GET A 123");
            Assert.fail("Accepted excess input");
        } catch (InvalidCommandException ignored) {
        }
    }

    @Test
    void rejectsWrongCommands() {
        try {
            CommandParser.createCommand("SEL A");
            Assert.fail("Accepted wrong input");
        } catch (InvalidCommandException ignored) {
        }
        try {
            CommandParser.createCommand("SEL");
            Assert.fail("Accepted wrong input");
        } catch (InvalidCommandException ignored) {
        }
        try {
            CommandParser.createCommand("SEL A 123");
            Assert.fail("Accepted wrong input");
        } catch (InvalidCommandException ignored) {
        }
    }

    @Test
    void rejectsFewArguments() {
        try {
            CommandParser.createCommand("DEL ");
            Assert.fail("Accepted input with few arguments");
        } catch (InvalidCommandException ignored) {
        }
        try {
            CommandParser.createCommand("SET A ");
            Assert.fail("Accepted input with few arguments");
        } catch (InvalidCommandException ignored) {
        }
    }

    @Test
    void rejectsBadNumber() {
        try {
            Assert.assertEquals(new ExpireCommand("A",120), CommandParser.createCommand("EXPIRE A abc"));
            Assert.fail("Accepted input with invalid argument format");
        } catch (InvalidCommandException ignored) {
        }
    }

    @Test
    void acceptsMultipleSpacedInput() {
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("DEL  A"));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("DEL   A"));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("  DEL A   "));
        Assert.assertEquals(new DelCommand("A"), CommandParser.createCommand("  DEL  A   "));
    }
}
