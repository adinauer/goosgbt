package at.dinauer.goosgbt;


import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


public class MainWindow
        extends
            JFrame {
    public class SnipersTableModel
            extends
                AbstractTableModel {
        private String statusText = STATUS_JOINING;
        
        public int getColumnCount() {
            return 1;
        }
        
        public int getRowCount() {
            return 1;
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            return statusText;
        }
        
        public void setStatusText(String statusText) {
            this.statusText = statusText;
            fireTableRowsUpdated(0, 0);
        }
    }
    
    public static final String      STATUS_JOINING     = "Joining";
    public static final String      STATUS_LOST        = "Lost";
    public static final String      STATUS_BIDDING     = "Bidding";
    public static final String      STATUS_WINNING     = "Winning";
    public static final String      STATUS_WON         = "Won";
    
    public static final String      SNIPERS_TABLE_NAME = "sniper table";
    public static final String      SNIPER_STATUS_NAME = "sniper status";
    
    private final SnipersTableModel snipers            = new SnipersTableModel();
    
    public MainWindow() {
        super("Auction Sniper");
        
        setName(Main.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }
    
    private JTable makeSnipersTable() {
        final JTable table = new JTable(snipers);
        
        table.setName(SNIPERS_TABLE_NAME);
        
        return table;
    }
    
    public void showStatus(String statusText) {
        snipers.setStatusText(statusText);
    }
}
