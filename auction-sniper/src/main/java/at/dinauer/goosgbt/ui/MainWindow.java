package at.dinauer.goosgbt.ui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import at.dinauer.goosgbt.Main;
import at.dinauer.goosgbt.SniperPortfolio;
import at.dinauer.goosgbt.UserRequestListener;
import at.dinauer.goosgbt.util.Announcer;


public class MainWindow
        extends
            JFrame {
    public static final String                   APPLICATION_TITLE        = "Auction Sniper";
    public static final String                   SNIPERS_TABLE_NAME       = "sniper table";
    public static final String                   SNIPER_STATUS_NAME       = "sniper status";
    public static final String                   NEW_ITEM_ID_NAME         = "new item id";
    public static final String                   JOIN_BUTTON_NAME         = "join button";
    public static final String                   NEW_ITEM_STOP_PRICE_NAME = "new item stop price";
    
    private final Announcer<UserRequestListener> userRequests             = Announcer.to(UserRequestListener.class);
    
    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        
        setName(Main.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private JPanel makeControls() {
        final JTextField itemIdField = itemIdField();
        final JFormattedTextField stopPriceField = stopPriceField();
        
        JPanel controls = new JPanel(new FlowLayout());
        controls.add(itemIdField);
        controls.add(stopPriceField);
        
        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        
        joinAuctionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });
        controls.add(joinAuctionButton);
        
        return controls;
    }
    
    private JTextField itemIdField() {
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        return itemIdField;
    }
    
    private JFormattedTextField stopPriceField() {
        final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        stopPriceField.setColumns(7);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        return stopPriceField;
    }
    
    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }
    
    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        
        final JTable table = new JTable(model);
        table.setName(SNIPERS_TABLE_NAME);
        
        return table;
    }
    
    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
}
