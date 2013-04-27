package at.dinauer.goosgbt;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;


final class XMPPAuction
        implements
            Auction {
    private final Chat chat;
    
    XMPPAuction(Chat chat) {
        this.chat = chat;
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
}