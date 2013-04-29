package at.dinauer.goosgbt;


public interface Auction {
    
    void bid(int amount);
    
    void join();
    
    void addAuctionEventListener(AuctionEventListener listener);
    
}
