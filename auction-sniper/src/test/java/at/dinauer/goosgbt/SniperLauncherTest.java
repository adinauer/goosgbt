package at.dinauer.goosgbt;


import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMock.class)
public class SniperLauncherTest {
    private final Mockery         context         = new JUnit4Mockery();
    private final States          auctionState    = context.states("auction state").startsAs("not joined");
    private final Auction         auction         = context.mock(Auction.class);
    private final AuctionHouse    auctionHouse    = context.mock(AuctionHouse.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private final SniperLauncher  launcher        = new SniperLauncher(auctionHouse, sniperCollector);
    
    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() {
        final String itemId = "item 123";
        
        context.checking(new Expectations() {
            {
                allowing(auctionHouse).auctionFor(itemId);
                will(returnValue(auction));
                
                oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));
                
                oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));
                
                oneOf(auction).join();
                then(auctionState.is("joined"));
            }
        });
        
        launcher.joinAuction(itemId);
    }
    
    protected Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}