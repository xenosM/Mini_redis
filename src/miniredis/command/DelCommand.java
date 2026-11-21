package miniredis.command;

import miniredis.store.Store;

public record DelCommand(String key) implements Command{

    @Override
    public String execute(Store<String, String> store) {
        return store.delete(key)? ReplyHelper.integer(1):ReplyHelper.integer(0);
    }
}
