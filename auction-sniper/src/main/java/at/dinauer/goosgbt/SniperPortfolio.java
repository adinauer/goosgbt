package at.dinauer.goosgbt;


import java.util.ArrayList;
import java.util.List;

import at.dinauer.goosgbt.util.Announcer;




public class SniperPortfolio
        implements
            SniperCollector {
    private Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);
    private List<AuctionSniper>          snipers   = new ArrayList<>();
    
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperAdded(sniper);
    }
    
    public void addPortfolioListener(PortfolioListener listener) {
        listeners.addListener(listener);
    }
}
