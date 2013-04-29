package at.dinauer.goosgbt;


import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;


public class SnipersTableModel
        extends
            AbstractTableModel
        implements
            SniperListener {
    private static String[]      STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won" };
    private List<SniperSnapshot> snapshots   = new ArrayList<>();
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return snapshots.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }
    
    public void sniperStateChanged(SniperSnapshot snapshot) {
        int row = rowMatching(snapshot);
        snapshots.set(row, snapshot);
        fireTableRowsUpdated(row, row);
    }
    
    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
    
    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }
    
    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        int rowIndex = getRowCount() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }
}