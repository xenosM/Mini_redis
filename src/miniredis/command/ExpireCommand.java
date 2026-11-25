package miniredis.command;

import miniredis.store.Store;

public record ExpireCommand(String key, long seconds) implements Command {

    @Override
    public String execute(Store<String, String> store) {
        return store.expire(key, seconds * 1000) ? ReplyHelper.integer(1) : ReplyHelper.integer(0);
    }
}
