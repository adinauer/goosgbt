package at.dinauer.goosgbt;


import java.util.EventListener;


public interface PortfolioListener
        extends
            EventListener {
    public void sniperAdded(AuctionSniper sniper);
}
