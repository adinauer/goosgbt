package at.dinauer.goosgbt.ui;

import javax.swing.SwingUtilities;

import at.dinauer.goosgbt.SniperListener;
import at.dinauer.goosgbt.SniperSnapshot;

public class SwingThreadSniperListener
        implements
            SniperListener {
    
    private final SnipersTableModel snipers;
    
    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }
    
    public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                snipers.sniperStateChanged(sniperSnapshot);
            }
        });
    }
}