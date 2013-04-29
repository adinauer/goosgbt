package at.dinauer.goosgbt;


import static org.hamcrest.Matchers.equalTo;

import org.junit.Ignore;
import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;


public class MainWindowTest {
    private final SnipersTableModel   tableModel = new SnipersTableModel();
    private final MainWindow          mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver     = new AuctionSniperDriver(100);
    
    @Ignore("does not close the window which causes other tests to break")
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
}
