package at.dinauer.goosgbt;


import javax.swing.table.AbstractTableModel;


public class SnipersTableModel
        extends
            AbstractTableModel
        implements
            SniperListener {
    private static String[]            STATUS_TEXT = {
                                                   "Joining", "Bidding", "Winning", "Lost", "Won" };
    public final static SniperSnapshot JOINING     = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot             snapshot    = JOINING;
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return 1;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }
    
    public void sniperStateChanged(SniperSnapshot snapshot) {
        this.snapshot = snapshot;
        fireTableRowsUpdated(0, 0);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
    
    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }
}