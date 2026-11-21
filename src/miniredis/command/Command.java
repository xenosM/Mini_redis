package miniredis.command;


import miniredis.store.Store;

/*
 * Even though the data store accepts generics for key and value
 * The command interface will only work on strings for both key and value
 * as the commands will be given using a text console that allows only strings
 * */
public interface Command {
    String execute(Store<String, String> store);
}
