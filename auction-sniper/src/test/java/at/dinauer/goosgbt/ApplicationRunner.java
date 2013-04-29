package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.FakeAuctionServer.XMPP_HOSTNAME;
import static at.dinauer.goosgbt.SniperState.LOST;


public class ApplicationRunner {
    public static final String  SNIPER_ID       = "sniper";
    public static final String  SNIPER_PASSWORD = "sniper";
    public static final String  SNIPER_XMPP_ID  = "sniper@localhost/Auction";
    
    private AuctionSniperDriver driver;
    
    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper(auctions);
        
        for (FakeAuctionServer auction : auctions) {
            final String itemId = auction.getItemId();
            driver.startBiddingFor(itemId);
            driver.showsSniperStatus(itemId, 0, 0, SniperState.JOINING);
        }
    }
    
    private void startSniper(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments(auctions));
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
    
    private String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        
        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 3] = auctions[i].getItemId();
        }
        
        return arguments;
    }
    
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(LOST);
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SniperState.BIDDING);
    }
    
    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SniperState.WINNING);
    }
    
    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, SniperState.WON);
    }
}
