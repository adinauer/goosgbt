package at.dinauer.goosgbt;


import javax.swing.table.AbstractTableModel;


public class SnipersTableModel
        extends
            AbstractTableModel {
    public enum Column {
        ITEM_IDENTIFIER,
        LAST_PRICE,
        LAST_BID,
        SNIPER_STATE;
        
        public static SnipersTableModel.Column at(int offset) {
            return values()[offset];
        }
    }
    
    private static String[]             STATUS_TEXT = {
                                                    "Joining", "Bidding", "Winning", "Lost", "Won" };
    // FIXME: should this really be initialized with state JOINING or should there be a NONE state?
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot              snapshot    = STARTING_UP;
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return 1;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return snapshot.itemId;
            case LAST_PRICE:
                return snapshot.lastPrice;
            case LAST_BID:
                return snapshot.lastBid;
            case SNIPER_STATE:
                return textFor(snapshot.state);
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }
    
    public void sniperStateChanged(SniperSnapshot snapshot) {
        this.snapshot = snapshot;
        fireTableRowsUpdated(0, 0);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
}