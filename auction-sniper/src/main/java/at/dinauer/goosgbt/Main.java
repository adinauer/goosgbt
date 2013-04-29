package at.dinauer.goosgbt;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import at.dinauer.goosgbt.ui.MainWindow;
import at.dinauer.goosgbt.ui.SnipersTableModel;
import at.dinauer.goosgbt.xmpp.XMPPAuctionHouse;


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
    
    private List<Auction>           notToBeGCd          = new ArrayList<>();
    
    public Main() throws Exception {
        startUserInterface();
    }
    
    public static void main(String... args)
            throws Exception {
        Main main = new Main();
        
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
                args[ARG_HOSTNAME],
                args[ARG_USERNAME],
                args[ARG_PASSWORD]);
        
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }
    
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                
                Auction auction = auctionHouse.auctionFor(itemId);
                notToBeGCd.add(auction);
                auction.addAuctionEventListener(
                        new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId));
                auction.join();
            }
        });
    }
    
    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
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
}
