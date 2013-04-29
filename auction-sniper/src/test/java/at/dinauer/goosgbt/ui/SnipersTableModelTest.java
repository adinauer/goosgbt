package at.dinauer.goosgbt.ui;


import static at.dinauer.goosgbt.ui.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.dinauer.goosgbt.AuctionSniper;
import at.dinauer.goosgbt.SniperSnapshot;
import at.dinauer.goosgbt.util.Defect;


@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery           context  = new JUnit4Mockery();
    private TableModelListener      listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model    = new SnipersTableModel();
    
    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }
    
    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }
    
    @Test
    public void setsSniperValuesInColumns() {
        AuctionSniper sniper = createSniperForItem("item id");
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
        
        context.checking(new Expectations() {
            {
                allowing(listener).tableChanged(with(anyInsertionEvent()));
                
                oneOf(listener).tableChanged(with(aChangeInRow(0)));
            }
        });
        
        
        model.addSniper(sniper);
        model.sniperStateChanged(bidding);
        
        assertRowMatchesSnapshot(0, bidding);
    }
    
    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }
    
    @Test
    public void notifiesListenersWhenAddingASniper() {
        AuctionSniper sniper = createSniperForItem("item id");
        
        context.checking(new Expectations() {
            {
                oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
            }
        });
        
        assertEquals(0, model.getRowCount());
        
        model.addSniper(sniper);
        
        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper.getSnapshot());
    }
    
    @Test
    public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {
            {
                ignoring(listener);
            }
        });
        
        model.addSniper(createSniperForItem("item 0"));
        model.addSniper(createSniperForItem("item 1"));
        
        assertCellEquals(0, Column.ITEM_IDENTIFIER, "item 0");
        assertCellEquals(1, Column.ITEM_IDENTIFIER, "item 1");
    }
    
    @Test
    public void updatesCorrectRowForSniper() {
        AuctionSniper s0 = createSniperForItem("item 0");
        AuctionSniper s1 = createSniperForItem("item 1");
        AuctionSniper s2 = createSniperForItem("item 2");
        
        
        context.checking(new Expectations() {
            {
                allowing(listener).tableChanged(with(anyInsertionEvent()));
                
                oneOf(listener).tableChanged(with(aChangeInRow(1)));
            }
        });
        
        model.addSniper(s0);
        model.addSniper(s1);
        model.addSniper(s2);
        
        model.sniperStateChanged(s1.getSnapshot());
    }
    
    @Test(expected = Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        AuctionSniper existing = createSniperForItem("existing item");
        AuctionSniper nonExisting = createSniperForItem("NONexisting item");
        
        context.checking(new Expectations() {
            {
                allowing(listener).tableChanged(with(anyInsertionEvent()));
            }
        });
        
        model.addSniper(existing);
        
        model.sniperStateChanged(nonExisting.getSnapshot());
    }
    
    private AuctionSniper createSniperForItem(String itemId) {
        return new AuctionSniper(null, itemId);
    }
    
    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
    
    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return allOf(anyInsertionEvent(), aChangeInRow(rowIndex));
    }
    
    private Matcher<TableModelEvent> aChangeInRow(int rowIndex) {
        return hasProperty("firstRow", equalTo(rowIndex));
    }
    
    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
        assertCellEquals(rowIndex, Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertCellEquals(rowIndex, Column.LAST_PRICE, snapshot.lastPrice);
        assertCellEquals(rowIndex, Column.LAST_BID, snapshot.lastBid);
        assertCellEquals(rowIndex, Column.SNIPER_STATE, textFor(snapshot.state));
    }
    
    private void assertCellEquals(int rowIndex, Column column, Object expected) {
        final int columnIndex = column.ordinal();
        
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}
