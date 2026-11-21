package miniredis.command;

import miniredis.store.Store;

public class GetCommand implements Command {
    private final String key ;

    public GetCommand(String key){
        this.key = key;
    }
    @Override
    public String execute(Store<String, String> store) {
        return store.get(key).map(ReplyHelper::bulk).orElse(ReplyHelper.nil());
    }
}
