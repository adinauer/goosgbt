package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.SniperState.BIDDING;
import static at.dinauer.goosgbt.SniperState.LOST;
import static at.dinauer.goosgbt.SniperState.WINNING;
import static at.dinauer.goosgbt.SniperState.WON;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.dinauer.goosgbt.AuctionEventListener.PriceSource;


@RunWith(JMock.class)
public class AuctionSniperTest {
    protected static final String ITEM_ID        = "itemID";
    private final Mockery         context        = new JUnit4Mockery();
    private final SniperListener  sniperListener = context.mock(SniperListener.class);
    private final Auction         auction        = context.mock(Auction.class);
    private final AuctionSniper   sniper         = new AuctionSniper(auction, ITEM_ID);
    private final States          sniperState    = context.states("sniper");
    
    @Before
    public void addSniperListener() {
        sniper.addSniperListener(sniperListener);
    }
    
    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatHas(LOST)));
            }
        });
        
        sniper.auctionClosed();
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatHas(LOST)));
                when(sniperState.is("bidding"));
            }
        });
        
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }
    
    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatHas(WON)));
                when(sniperState.is("winning"));
            }
        });
        
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
    
    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        
        context.checking(new Expectations() {
            {
                oneOf(auction).bid(bid);
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
            }
        });
        
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
                when(sniperState.is("bidding"));
            }
        });
        
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }
    
    private Matcher<SniperSnapshot> aSniperThatHas(final SniperState state) {
        return aSniperThatIs(state);
    }
    
    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
