package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.Main.MAIN_WINDOW_NAME;
import static at.dinauer.goosgbt.ui.MainWindow.JOIN_BUTTON_NAME;
import static at.dinauer.goosgbt.ui.MainWindow.NEW_ITEM_ID_NAME;
import static at.dinauer.goosgbt.ui.MainWindow.NEW_ITEM_STOP_PRICE_NAME;
import static at.dinauer.goosgbt.ui.SnipersTableModel.textFor;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;


public class AuctionSniperDriver
        extends
            JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MAIN_WINDOW_NAME),
                        showingOnScreen()
                        ),
                new AWTEventQueueProber(timeoutMillis, 100));
    }
    
    public void showsSniperStatus(Item item, int lastPrice, int lastBid, SniperState state) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching(
                withLabelText(item.identifier),
                withLabelText(valueOf(lastPrice)),
                withLabelText(valueOf(lastBid)),
                withLabelText(textFor(state))));
    }
    
    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(
                withLabelText("Item"),
                withLabelText("Last Price"),
                withLabelText("Last Bid"),
                withLabelText("State")));
    }
    
    public void startBiddingFor(Item item) {
        textField(NEW_ITEM_ID_NAME).replaceAllText(item.identifier);
        textField(NEW_ITEM_STOP_PRICE_NAME).replaceAllText(valueOf(item.stopPrice));
        bidButton().click();
    }
    
    private JTextFieldDriver textField(String textFieldName) {
        JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(textFieldName));
        
        newItemId.focusWithMouse();
        
        return newItemId;
    }
    
    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(JOIN_BUTTON_NAME));
    }
}
