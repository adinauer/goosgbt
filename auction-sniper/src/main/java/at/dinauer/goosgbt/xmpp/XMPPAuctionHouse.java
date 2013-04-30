package at.dinauer.goosgbt.xmpp;


import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.Auction;
import at.dinauer.goosgbt.AuctionHouse;
import at.dinauer.goosgbt.Item;


public class XMPPAuctionHouse
        implements
            AuctionHouse {
    
    public static final String  AUCTION_RESOURCE  = "Auction";
    public static final String  ITEM_ID_AS_LOGIN  = "auction-%s";
    public static final String  AUCTION_ID_FORMAT = XMPPAuctionHouse.ITEM_ID_AS_LOGIN
                                                          + "@%s/"
                                                          + XMPPAuctionHouse.AUCTION_RESOURCE;
    private static final String LOGGER_NAME       = "auction-sniper";
    public static final String  LOG_FILE_NAME     = "auction-sniper.log";
    
    private XMPPConnection      connection;
    private XMPPFailureReporter failureReporter;
    
    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
        failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }
    
    private Logger makeLogger() {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        
        return logger;
    }
    
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item, connection), failureReporter);
    }
    
    private Handler simpleFileHandler() {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new XMPPAuctionException("Could not create logger FileHandler "
                    + FilenameUtils.getFullPath(LOG_FILE_NAME), e);
        }
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
    
    private static String auctionId(Item item, XMPPConnection connection) {
        return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, item.identifier, connection.getServiceName());
    }
}
