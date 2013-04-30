package at.dinauer.goosgbt.ui;


import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.Test;

import at.dinauer.goosgbt.AuctionSniperDriver;
import at.dinauer.goosgbt.Item;
import at.dinauer.goosgbt.SniperPortfolio;
import at.dinauer.goosgbt.UserRequestListener;

import com.objogate.wl.swing.probe.ValueMatcherProbe;


public class MainWindowTest {
    private final SniperPortfolio     portfolio  = new SniperPortfolio();
    private final MainWindow          mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver     = new AuctionSniperDriver(100);
    
    @Test
    public void makeUsersRequestWhenJoinButtonClicked() {
        final Item item = Item.createWithoutStopPrice("an item-id");
        final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(item), "join request");
        
        mainWindow.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(Item item) {
                itemProbe.setReceivedValue(item);
            }
        });
        
        driver.startBiddingFor(item);
        driver.check(itemProbe);
    }
    
    @After
    public void stopAuction() {
        mainWindow.dispose();
    }
}
