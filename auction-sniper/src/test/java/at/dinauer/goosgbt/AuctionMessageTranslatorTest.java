package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.ApplicationRunner.SNIPER_ID;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.dinauer.goosgbt.AuctionEventListener.PriceSource;


@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
    public static final Chat               UNUSED_CHAT = null;
    private final Mockery                  context     = new JUnit4Mockery();
    private final AuctionEventListener     listener    = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator  = new AuctionMessageTranslator(SNIPER_ID, listener);
    
    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {
            {
                oneOf(listener).auctionClosed();
            }
        });
        
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
    
    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        context.checking(new Expectations() {
            {
                oneOf(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
            }
        });
        
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
    
    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        context.checking(new Expectations() {
            {
                oneOf(listener).currentPrice(234, 5, PriceSource.FromSniper);
            }
        });
        
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
}
