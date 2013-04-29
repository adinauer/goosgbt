package at.dinauer.goosgbt;

public class SniperSnapshot {
    public final String      itemId;
    public final int         lastPrice;
    public final int         lastBid;
    public final SniperState state;
    
    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState sniperState) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        state = sniperState;
    }
    
    public SniperSnapshot bidding(int lastPrice, int lastBid) {
        return new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.BIDDING);
    }
    
    public SniperSnapshot winning(int lastPrice) {
        return new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.WINNING);
    }
    
    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }
    
    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
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
        if (itemId == null) {
            if (other.itemId != null) {
                return false;
            }
        } else if (!itemId.equals(other.itemId)) {
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
        return "SniperSnapshot [itemId="
                + itemId
                + ", lastPrice="
                + lastPrice
                + ", lastBid="
                + lastBid
                + ", sniperState="
                + state
                + "]";
    }
    
    public boolean isForSameItemAs(SniperSnapshot otherSnapshot) {
        return itemId.equals(otherSnapshot.itemId);
    }
    
}
