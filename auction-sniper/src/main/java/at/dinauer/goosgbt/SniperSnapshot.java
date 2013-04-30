package at.dinauer.goosgbt;

public class SniperSnapshot {
    public final Item        item;
    public final int         lastPrice;
    public final int         lastBid;
    public final SniperState state;
    
    public SniperSnapshot(Item item, int lastPrice, int lastBid, SniperState sniperState) {
        this.item = item;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        state = sniperState;
    }
    
    public SniperSnapshot bidding(int lastPrice, int lastBid) {
        return new SniperSnapshot(item, lastPrice, lastBid, SniperState.BIDDING);
    }
    
    public SniperSnapshot winning(int lastPrice) {
        return new SniperSnapshot(item, lastPrice, lastBid, SniperState.WINNING);
    }
    
    public SniperSnapshot losing(int lastPrice) {
        return new SniperSnapshot(item, lastPrice, lastBid, SniperState.LOSING);
    }
    
    public static SniperSnapshot joining(Item item) {
        return new SniperSnapshot(item, 0, 0, SniperState.JOINING);
    }
    
    public SniperSnapshot closed() {
        return new SniperSnapshot(item, lastPrice, lastBid, state.whenAuctionClosed());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + lastBid;
        result = prime * result + lastPrice;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SniperSnapshot other = (SniperSnapshot) obj;
        if (item == null) {
            if (other.item != null) {
                return false;
            }
        } else if (!item.equals(other.item)) {
            return false;
        }
        if (lastBid != other.lastBid) {
            return false;
        }
        if (lastPrice != other.lastPrice) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "SniperSnapshot [item="
                + item
                + ", lastPrice="
                + lastPrice
                + ", lastBid="
                + lastBid
                + ", sniperState="
                + state
                + "]";
    }
    
    public boolean isForSameItemAs(SniperSnapshot otherSnapshot) {
        return item.equals(otherSnapshot.item);
    }
}
