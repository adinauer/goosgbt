package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.ApplicationRunner.SNIPER_XMPP_ID;

import org.junit.After;
import org.junit.Test;


public class AuctionSniperEndToEndTest {
    private final Item              item        = Item.createWithoutStopPrice("item-54321");
    private FakeAuctionServer       auction     = new FakeAuctionServer(item);
    private final Item              item2       = Item.createWithoutStopPrice("item-65432");
    private final FakeAuctionServer auction2    = new FakeAuctionServer(item2);
    private final ApplicationRunner application = new ApplicationRunner();
    
    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 0, 0);
    }
    
    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        
        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 1000, 1098);
    }
    
    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction, 1098);
        
        auction.announceClosed();
        application.showsSniperHasWonAuction(auction, 1098);
    }
    
    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();
        auction2.startSellingItem();
        
        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        
        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, SNIPER_XMPP_ID);
        
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, SNIPER_XMPP_ID);
        
        application.hasShownSniperIsWinning(auction, 1098);
        application.hasShownSniperIsWinning(auction2, 521);
        
        auction.announceClosed();
        auction2.announceClosed();
        
        application.showsSniperHasWonAuction(auction, 1098);
        application.showsSniperHasWonAuction(auction2, 521);
    }
    
    @Test
    public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
        Item itemWithStopPrice = Item.createWithStopPrice("item-54321", 1100);
        auction = new FakeAuctionServer(itemWithStopPrice);
        
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        
        auction.reportPrice(1197, 10, "third party");
        application.hasShownSniperIsLosing(auction, 1197, 1098);
        
        auction.reportPrice(1207, 10, "fourth party");
        application.hasShownSniperIsLosing(auction, 1207, 1098);
        
        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 1207, 1098);
    }
    
    @Test
    public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
        String brokenMessage = "a broken message";
        auction.startSellingItem();
        auction2.startSellingItem();
        
        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        
        auction.reportPrice(500, 20, "other bidder");
        auction.hasReceivedBid(520, SNIPER_XMPP_ID);
        
        auction.sendInvalidMessageContaining(brokenMessage);
        application.showsSniperHasFailed(auction);
        
        auction.reportPrice(520, 21, "other bidder");
        waitForAnotherAuctionEvent();
        
        application.reportsInvalidMessage(auction, brokenMessage);
        application.showsSniperHasFailed(auction);
    }
    
    private void waitForAnotherAuctionEvent() throws Exception {
        auction2.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
        auction2.reportPrice(600, 6, "other bidder");
        application.hasShownSniperIsBidding(auction2, 600, 606);
    }
    
    @After
    public void stopAuction() {
        auction.stop();
        auction2.stop();
    }
    
    @After
    public void stopApplication() {
        application.stop();
    }
}
