package miniredis.command;

public class ReplyHelper {
    private ReplyHelper(){

    }

    static String ok(){ return "OK";}
    static String bulk(String value){return "\""+value+"\"";}
    static String nil(){ return "(nil)";}
    static String integer(long n) { return "(integer)"+ n;}
    static String error(String msg){ return "(error) ERROR: "+msg;}
}
