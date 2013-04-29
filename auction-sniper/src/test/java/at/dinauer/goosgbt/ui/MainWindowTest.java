package at.dinauer.goosgbt.ui;


import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.Test;

import at.dinauer.goosgbt.AuctionSniperDriver;
import at.dinauer.goosgbt.UserRequestListener;

import com.objogate.wl.swing.probe.ValueMatcherProbe;


public class MainWindowTest {
    private final SnipersTableModel   tableModel = new SnipersTableModel();
    private final MainWindow          mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver     = new AuctionSniperDriver(100);
    
    @Test
    public void makeUsersRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("an item-id"), "join request");
        
        mainWindow.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });
        
        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
    
    @After
    public void stopAuction() {
        mainWindow.dispose();
    }
}
