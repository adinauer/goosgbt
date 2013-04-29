package at.dinauer.goosgbt.xmpp;


import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.Auction;
import at.dinauer.goosgbt.AuctionHouse;
import at.dinauer.goosgbt.Main;


public class XMPPAuctionHouse
        implements
            AuctionHouse {
    
    private XMPPConnection connection;
    
    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }
    
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, itemId);
    }
    
    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        
        connection.connect();
        connection.login(username, password, Main.AUCTION_RESOURCE);
        
        
        return new XMPPAuctionHouse(connection);
    }
    
    public void disconnect() {
        connection.disconnect();
    }
}
