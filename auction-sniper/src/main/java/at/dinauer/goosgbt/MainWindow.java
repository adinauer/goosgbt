package at.dinauer.goosgbt;


import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class MainWindow
        extends
            JFrame {
    public static final String      APPLICATION_TITLE  = "Auction Sniper";
    public static final String      SNIPERS_TABLE_NAME = "sniper table";
    public static final String      SNIPER_STATUS_NAME = "sniper status";
    
    private final SnipersTableModel snipers;
    
    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        
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
}
