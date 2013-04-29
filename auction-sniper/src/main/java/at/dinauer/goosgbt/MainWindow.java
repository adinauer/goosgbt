package at.dinauer.goosgbt;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class MainWindow
        extends
            JFrame {
    public static final String      APPLICATION_TITLE  = "Auction Sniper";
    public static final String      SNIPERS_TABLE_NAME = "sniper table";
    public static final String      SNIPER_STATUS_NAME = "sniper status";
    public static final String      NEW_ITEM_ID_NAME   = "new item id";
    public static final String      JOIN_BUTTON_NAME   = "join button";
    
    private final SnipersTableModel snipers;
    
    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        
        setName(Main.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(), makeControls());
        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        
        JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);
        
        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);
        
        return controls;
    }
    
    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }
    
    private JTable makeSnipersTable() {
        final JTable table = new JTable(snipers);
        
        table.setName(SNIPERS_TABLE_NAME);
        
        return table;
    }
}
