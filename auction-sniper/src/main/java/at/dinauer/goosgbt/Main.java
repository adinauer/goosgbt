package at.dinauer.goosgbt;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import at.dinauer.goosgbt.ui.MainWindow;
import at.dinauer.goosgbt.ui.SnipersTableModel;
import at.dinauer.goosgbt.xmpp.XMPPAuction;


public class Main {
    public class SwingThreadSniperListener
            implements
                SniperListener {
        
        private final SnipersTableModel snipers;
        
        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }
        
        public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    snipers.sniperStateChanged(sniperSnapshot);
                }
            });
        }
    }
    
    private static final int        ARG_HOSTNAME        = 0;
    private static final int        ARG_USERNAME        = 1;
    private static final int        ARG_PASSWORD        = 2;
    private static final int        ARG_ITEM_ID_START   = 3;
    
    public static final String      MAIN_WINDOW_NAME    = "Auction Sniper MAIN";
    public static final String      AUCTION_RESOURCE    = "Auction";
    public static final String      ITEM_ID_AS_LOGIN    = "auction-%s";
    public static final String      AUCTION_ID_FORMAT   = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    public static final String      BID_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: BID; Price %d;";
    public static final String      JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    
    private final SnipersTableModel snipers             = new SnipersTableModel();
    private MainWindow              ui;
    
    /* keep a reference to the chat to avoid garbage collection */
    private List<Chat>              notToBeGCd          = new ArrayList<>();
    
    public Main() throws Exception {
        startUserInterface();
    }
    
    public static void main(String... args)
            throws Exception {
        Main main = new Main();
        
        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }
    
    private void addUserRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                
                Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
                notToBeGCd.add(chat);
                
                Auction auction = new XMPPAuction(chat);
                chat.addMessageListener(new AuctionMessageTranslator(
                        connection.getUser(),
                        new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId)));
                
                auction.join();
            }
        });
    }
    
    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }
    
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }
    
    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        
        return connection;
    }
    
    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
