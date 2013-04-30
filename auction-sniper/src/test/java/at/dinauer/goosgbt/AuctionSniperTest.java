package at.dinauer.goosgbt;


import static at.dinauer.goosgbt.SniperState.BIDDING;
import static at.dinauer.goosgbt.SniperState.LOSING;
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
    private final Mockery        context        = new JUnit4Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction        auction        = context.mock(Auction.class);
    private final Item           item           = Item.createWithoutStopPrice("itemID");
    private AuctionSniper        sniper         = new AuctionSniper(auction, item);
    private final States         sniperState    = context.states("sniper");
    
    @Before
    public void createSniper() {
        createSniperForItem(item);
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
                        new SniperSnapshot(item, price, bid, SniperState.BIDDING));
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
                
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item, 135, 135, WINNING));
                when(sniperState.is("bidding"));
            }
        });
        
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        final Item itemWithStopPrice = Item.createWithStopPrice("item 1", 1234);
        createSniperForItem(itemWithStopPrice);
        
        allowingSniperBidding();
        
        context.checking(new Expectations() {
            {
                int bid = 123 + 45;
                
                allowing(auction).bid(bid);
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(itemWithStopPrice, 2345, bid, LOSING));
                when(sniperState.is("bidding"));
            }
        });
        
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        final Item itemWithStopPrice = Item.createWithStopPrice("item 0", 1000);
        createSniperForItem(itemWithStopPrice);
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(itemWithStopPrice, 999, 0, LOSING));
            }
        });
        
        sniper.currentPrice(999, 2, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        final Item itemWithStopPrice = Item.createWithStopPrice("item 0", 1000);
        createSniperForItem(itemWithStopPrice);
        
        context.checking(new Expectations() {
            {
                ignoring(auction);
                
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(LOSING)));
                then(sniperState.is("losing"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatHas(LOST)));
                when(sniperState.is("losing"));
            }
        });
        
        sniper.currentPrice(999, 2, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }
    
    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Item itemWithStopPrice = Item.createWithStopPrice("item 1", 1000);
        createSniperForItem(itemWithStopPrice);
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(itemWithStopPrice, 2345, 0, LOSING));
                then(sniperState.is("losing"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(itemWithStopPrice, 2370, 0, LOSING));
                when(sniperState.is("losing"));
            }
        });
        
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        sniper.currentPrice(2370, 30, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        final Item itemWithStopPrice = Item.createWithStopPrice("item 1", 1000);
        createSniperForItem(itemWithStopPrice);
        
        context.checking(new Expectations() {
            {
                ignoring(auction);
                
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));
                
                atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatHas(LOSING)));
                when(sniperState.is("winning"));
            }
        });
        
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(1100, 50, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() {
        ignoringAuction();
        allowingSniperBidding();
        
        expectSniperToFailWhenItIs("bidding");
        
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionFailed();
    }
    
    private void expectSniperToFailWhenItIs(final String state) {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item, 0, 0, SniperState.FAILED));
                when(sniperState.is(state));
            }
        });
    }
    
    private void ignoringAuction() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
            }
        });
    }
    
    private void allowingSniperBidding() {
        context.checking(new Expectations() {
            {
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));
            }
        });
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
    
    private void createSniperForItem(final Item itemWithStopPrice) {
        sniper = new AuctionSniper(auction, itemWithStopPrice);
        sniper.addSniperListener(sniperListener);
    }
}
