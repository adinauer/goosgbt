package at.dinauer.goosgbt.xmpp;


import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.Auction;
import at.dinauer.goosgbt.AuctionHouse;


public class XMPPAuctionHouse
        implements
            AuctionHouse {
    
    public static final String AUCTION_RESOURCE  = "Auction";
    public static final String ITEM_ID_AS_LOGIN  = "auction-%s";
    public static final String AUCTION_ID_FORMAT = XMPPAuctionHouse.ITEM_ID_AS_LOGIN
                                                         + "@%s/"
                                                         + XMPPAuctionHouse.AUCTION_RESOURCE;
    
    private XMPPConnection     connection;
    
    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }
    
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId, connection));
    }
    
    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        
        connection.connect();
        connection.login(username, password, XMPPAuctionHouse.AUCTION_RESOURCE);
        
        
        return new XMPPAuctionHouse(connection);
    }
    
    public void disconnect() {
        connection.disconnect();
    }
    
    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}