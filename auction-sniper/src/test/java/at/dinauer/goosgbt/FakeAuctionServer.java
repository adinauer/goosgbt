package at.dinauer.goosgbt;


import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.xmpp.XMPPAuctionHouse;


public class FakeAuctionServer {
    public static final String          XMPP_HOSTNAME    = "localhost";
    public static final String          AUCTION_PASSWORD = "auction";
    
    private final Item                  item;
    private final XMPPConnection        connection;
    private Chat                        currentChat;
    private final SingleMessageListener messageListener  = new SingleMessageListener();
    
    public FakeAuctionServer(Item item) {
        this.item = item;
        connection = new XMPPConnection(XMPP_HOSTNAME);
    }
    
    public void startSellingItem()
            throws XMPPException {
        connection.connect();
        connection.login(
                format(XMPPAuctionHouse.ITEM_ID_AS_LOGIN, item.identifier),
                AUCTION_PASSWORD,
                XMPPAuctionHouse.AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            public void chatCreated(Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }
    
    public Item getItem() {
        return item;
    }
    
    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }
    
    public void stop() {
        connection.disconnect();
    }
    
    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(String.format(
                "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;",
                price,
                increment,
                bidder));
    }
    
    public void hasReceivedJoinRequestFromSniper(String sniperId)
            throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }
    
    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
    }
    
    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }
    
    public void sendInvalidMessageContaining(String message) throws XMPPException {
        currentChat.sendMessage(message);
    }
}
