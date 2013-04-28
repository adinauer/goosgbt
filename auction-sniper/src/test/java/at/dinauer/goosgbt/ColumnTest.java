package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class ColumnTest {
    private final SniperSnapshot snapshot = new SniperSnapshot("item id", 123, 45, SniperState.WINNING);
    
    @Test
    public void returnsItemId() {
        assertColumnValueMatches(Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertColumnValueMatches(Column.LAST_BID, snapshot.lastBid);
        assertColumnValueMatches(Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnValueMatches(Column.SNIPER_STATE, textFor(snapshot.state));
    }
    
    private void assertColumnValueMatches(Column column, Object field) {
        assertThat(Column.at(column.ordinal()).valueIn(snapshot), equalTo(field));
    }
}
