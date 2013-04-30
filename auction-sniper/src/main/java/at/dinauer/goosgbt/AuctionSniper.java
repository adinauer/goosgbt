package at.dinauer.goosgbt;


public class AuctionSniper
        implements
            AuctionEventListener {
    
    private SniperListener sniperListener;
    private final Auction  auction;
    private SniperSnapshot snapshot;
    
    public AuctionSniper(Auction auction, Item item) {
        this.auction = auction;
        snapshot = SniperSnapshot.joining(item);
    }
    
    public void auctionClosed() {
        snapshot = snapshot.closed();
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
        
        if (bid > snapshot.item.stopPrice) {
            snapshot = snapshot.losing(price);
        } else {
            auction.bid(price + increment);
            snapshot = snapshot.bidding(price, bid);
        }
    }
    
    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }
    
    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
    
    public void addSniperListener(SniperListener listener) {
        sniperListener = listener;
    }
}
