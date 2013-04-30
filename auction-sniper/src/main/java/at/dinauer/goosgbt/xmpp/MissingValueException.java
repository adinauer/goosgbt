package at.dinauer.goosgbt.xmpp;

public class MissingValueException
        extends
            RuntimeException {
    
    public MissingValueException(String fieldName) {
        super("No value found for field: " + fieldName);
    }
    
}
