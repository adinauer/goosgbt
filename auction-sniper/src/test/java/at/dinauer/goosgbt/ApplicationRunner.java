package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.FakeAuctionServer.XMPP_HOSTNAME;
import at.dinauer.goosgbt.ui.MainWindow;


public class ApplicationRunner {
    public static final String  SNIPER_ID       = "sniper";
    public static final String  SNIPER_PASSWORD = "sniper";
    public static final String  SNIPER_XMPP_ID  = "sniper@localhost/Auction";
    
    private AuctionSniperDriver driver;
    
    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        
        for (FakeAuctionServer auction : auctions) {
            final Item item = auction.getItem();
            driver.startBiddingFor(item);
            driver.showsSniperStatus(item, 0, 0, SniperState.JOINING);
        }
    }
    
    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
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
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItem(), lastPrice, lastBid, SniperState.BIDDING);
    }
    
    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItem(), winningBid, winningBid, SniperState.WINNING);
    }
    
    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItem(), lastPrice, lastPrice, SniperState.WON);
    }
    
    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItem(), lastPrice, lastBid, SniperState.LOSING);
    }
    
    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItem(), lastPrice, lastBid, SniperState.LOST);
    }
    
    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItem(), 0, 0, SniperState.FAILED);
    }
    
    public void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) {
        // TODO Auto-generated method stub
    }
}
