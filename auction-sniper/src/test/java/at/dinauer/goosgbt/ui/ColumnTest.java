package at.dinauer.goosgbt.ui;


import static at.dinauer.goosgbt.ui.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import at.dinauer.goosgbt.Item;
import at.dinauer.goosgbt.SniperSnapshot;
import at.dinauer.goosgbt.SniperState;


public class ColumnTest {
    private final Item           item     = Item.createWithoutStopPrice("item id");
    private final SniperSnapshot snapshot = new SniperSnapshot(item, 123, 45, SniperState.WINNING);
    
    @Test
    public void returnsItemId() {
        assertColumnValueMatches(Column.ITEM_IDENTIFIER, snapshot.item.identifier);
        assertColumnValueMatches(Column.LAST_BID, snapshot.lastBid);
        assertColumnValueMatches(Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnValueMatches(Column.SNIPER_STATE, textFor(snapshot.state));
    }
    
    private void assertColumnValueMatches(Column column, Object field) {
        assertThat(Column.at(column.ordinal()).valueIn(snapshot), equalTo(field));
    }
}
