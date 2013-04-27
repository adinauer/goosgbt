package at.dinauer.goosgbt;


import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;


public class FakeAuctionServer {
    public static final String          XMPP_HOSTNAME    = "localhost";
    public static final String          AUCTION_PASSWORD = "auction";

    private final String                itemId;
    private final XMPPConnection        connection;
    private Chat                        currentChat;
    private final SingleMessageListener messageListener  = new SingleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem()
            throws XMPPException {
        connection.connect();
        connection.login(format(Main.ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, Main.AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            public void chatCreated(Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }

    public String getItemId() {
        return itemId;
    }

    public void hasReceivedJoinRequestFromSniper()
            throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed()
            throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }
}
