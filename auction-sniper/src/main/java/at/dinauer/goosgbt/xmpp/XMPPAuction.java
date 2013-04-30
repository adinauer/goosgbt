package at.dinauer.goosgbt.xmpp;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.Auction;
import at.dinauer.goosgbt.AuctionEventListener;
import at.dinauer.goosgbt.Main;
import at.dinauer.goosgbt.util.Announcer;


public class XMPPAuction
        implements
            Auction {
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat                            chat;
    
    public XMPPAuction(XMPPConnection connection, String auctionId) {
        AuctionMessageTranslator translator = translateFor(connection);
        chat = connection.getChatManager().createChat(auctionId, translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }
    
    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }
            
            public void auctionClosed() {}
            
            public void currentPrice(int price, int increment, PriceSource bidder) {}
            
        };
    }
    
    private AuctionMessageTranslator translateFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce());
    }
    
    public void bid(int amount) {
        sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
    }
    
    public void join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT);
    }
    
    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
    
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }
}