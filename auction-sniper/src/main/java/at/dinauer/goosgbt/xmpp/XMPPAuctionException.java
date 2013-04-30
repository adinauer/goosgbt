package at.dinauer.goosgbt.xmpp;

public class XMPPAuctionException
        extends
            RuntimeException {
    
    public XMPPAuctionException(String message, Exception exception) {
        super(message, exception);
    }
    
}
