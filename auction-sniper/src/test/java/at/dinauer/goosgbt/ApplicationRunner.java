package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.FakeAuctionServer.XMPP_HOSTNAME;
import static at.dinauer.goosgbt.SniperState.LOST;
import static at.dinauer.goosgbt.SnipersTableModel.JOINING;


public class ApplicationRunner {
    public static final String  SNIPER_ID       = "sniper";
    public static final String  SNIPER_PASSWORD = "sniper";
    public static final String  SNIPER_XMPP_ID  = "sniper@localhost/Auction";
    
    private AuctionSniperDriver driver;
    private String              itemId;
    
    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        thread.setDaemon(true);
        thread.start();
        
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus(JOINING.itemId, JOINING.lastPrice, JOINING.lastBid, SniperState.JOINING);
    }
    
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(LOST);
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SniperState.BIDDING);
    }
    
    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, SniperState.WINNING);
    }
    
    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, SniperState.WON);
    }
}
