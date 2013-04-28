package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.SnipersTableModel.textFor;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;


public class AuctionSniperDriver
        extends
            JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MAIN_WINDOW_NAME),
                        showingOnScreen()
                        ),
                new AWTEventQueueProber(timeoutMillis, 100));
    }
    
    public void showsSniperStatus(SniperState state) {
        new JTableDriver(this).hasCell(withLabelText(equalTo(textFor(state))));
    }
    
    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, SniperState state) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching(
                withLabelText(itemId),
                withLabelText(valueOf(lastPrice)),
                withLabelText(valueOf(lastBid)),
                withLabelText(textFor(state))));
    }
}
