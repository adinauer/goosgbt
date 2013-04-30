package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.xmpp.XMPPAuctionHouse.LOG_FILE_NAME;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;


public class AuctionLogDriver {
    private final File logFile = new File(LOG_FILE_NAME);
    
    public void clearLog() {
        logFile.delete();
        LogManager.getLogManager().reset();
    }
    
    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(FileUtils.readFileToString(logFile), matcher);
    }
}
