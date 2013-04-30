package at.dinauer.goosgbt.xmpp;


import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.dinauer.goosgbt.ApplicationRunner;
import at.dinauer.goosgbt.Auction;
import at.dinauer.goosgbt.AuctionEventListener;
import at.dinauer.goosgbt.FakeAuctionServer;
import at.dinauer.goosgbt.Item;


public class XMPPAuctionHouseTest {
    private Item              item          = Item.createWithoutStopPrice("item-54321");
    private FakeAuctionServer auctionServer = new FakeAuctionServer(item);
    private XMPPAuctionHouse  auctionHouse;
    
    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        
        Auction auction = auctionHouse.auctionFor(auctionServer.getItem());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
        
        auction.join();
        auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();
        
        assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
    }
    
    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }
            
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // not implemented
            }
        };
    }
    
    @Before
    public void openConnection() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(
                FakeAuctionServer.XMPP_HOSTNAME,
                ApplicationRunner.SNIPER_ID,
                ApplicationRunner.SNIPER_PASSWORD);
    }
    
    @Before
    public void startAuction() throws Exception {
        auctionServer.startSellingItem();
    }
    
    @After
    public void closeConnection() {
        if (auctionHouse != null) {
            auctionHouse.disconnect();
        }
    }
    
    @After
    public void stopAuction() {
        auctionServer.stop();
    }
}
