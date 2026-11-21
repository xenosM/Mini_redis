package miniredis.command;

import miniredis.store.Store;

public record TtlCommand(String key) implements Command {

    @Override
    public String execute(Store<String, String> store) {
        long ttl = store.ttl(key);
        if (ttl > 0) {
            ttl = (ttl + 500)/1000;
        }
        return ReplyHelper.integer(ttl);
    }
}
