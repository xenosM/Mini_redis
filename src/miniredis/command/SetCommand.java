package miniredis.command;

import miniredis.store.Store;

public record SetCommand(String key, String value) implements Command {

    @Override
    public String execute(Store<String, String> store) {
        store.put(key,value);
        return ReplyHelper.ok();
    }
}
