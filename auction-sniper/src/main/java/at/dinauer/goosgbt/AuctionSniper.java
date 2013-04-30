package at.dinauer.goosgbt;


import at.dinauer.goosgbt.util.Announcer;


public class AuctionSniper
        implements
            AuctionEventListener {
    
    private Announcer<SniperListener> sniperListeners = Announcer.to(SniperListener.class);
    private final Auction             auction;
    private SniperSnapshot            snapshot;
    private final Item                item;
    
    public AuctionSniper(Auction auction, Item item) {
        this.auction = auction;
        this.item = item;
        snapshot = SniperSnapshot.joining(item);
    }
    
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }
    
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    }
    
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                handlePriceUpdateFromSniper(price);
                break;
            case FromOtherBidder:
                handlePriceUpdateFromOtherBidder(price, increment);
                break;
        }
        
        notifyChange();
    }
    
    private void handlePriceUpdateFromSniper(int price) {
        snapshot = snapshot.winning(price);
    }
    
    private void handlePriceUpdateFromOtherBidder(int price, int increment) {
        int bid = price + increment;
        
        if (item.allowsBid(bid)) {
            auction.bid(price + increment);
            snapshot = snapshot.bidding(price, bid);
        } else {
            snapshot = snapshot.losing(price);
        }
    }
    
    private void notifyChange() {
        sniperListeners.announce().sniperStateChanged(snapshot);
    }
    
    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
    
    public void addSniperListener(SniperListener listener) {
        sniperListeners.addListener(listener);
    }
}
