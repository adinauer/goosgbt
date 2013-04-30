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
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                int bid = price + increment;
                auction.bid(price + increment);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        
        notifyChange();
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
