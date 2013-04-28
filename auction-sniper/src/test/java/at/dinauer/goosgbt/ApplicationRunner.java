package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.FakeAuctionServer.XMPP_HOSTNAME;
import static at.dinauer.goosgbt.MainWindow.STATUS_BIDDING;
import static at.dinauer.goosgbt.MainWindow.STATUS_JOINING;
import static at.dinauer.goosgbt.MainWindow.STATUS_LOST;
import static at.dinauer.goosgbt.MainWindow.STATUS_WINNING;
import static at.dinauer.goosgbt.MainWindow.STATUS_WON;


public class ApplicationRunner {
    public static final String  SNIPER_ID       = "sniper";
    public static final String  SNIPER_PASSWORD = "sniper";
    public static final String  SNIPER_XMPP_ID  = "sniper@localhost/Auction";
    
    private AuctionSniperDriver driver;
    
    public void startBiddingIn(final FakeAuctionServer auction) {
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
        driver.showsSniperStatus(STATUS_JOINING);
    }
    
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING);
    }
    
    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(STATUS_WINNING);
    }
    
    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(STATUS_WON);
    }
}
