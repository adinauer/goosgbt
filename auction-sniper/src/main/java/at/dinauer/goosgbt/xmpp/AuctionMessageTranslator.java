package at.dinauer.goosgbt.xmpp;


import static at.dinauer.goosgbt.AuctionEventListener.PriceSource.FromOtherBidder;
import static at.dinauer.goosgbt.AuctionEventListener.PriceSource.FromSniper;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import at.dinauer.goosgbt.AuctionEventListener;
import at.dinauer.goosgbt.AuctionEventListener.PriceSource;


public class AuctionMessageTranslator
        implements
            MessageListener {
    
    public static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();
        
        
        public String type() {
            return get("Event");
        }
        
        public int increment() {
            return getInt("Increment");
        }
        
        public int currentPrice() {
            return getInt("CurrentPrice");
        }
        
        private String bidder() {
            return get("Bidder");
        }
        
        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }
        
        private String get(String fieldName) {
            String field = fields.get(fieldName);
            if (null == field) {
                throw new MissingValueException(fieldName);
            }
            return field;
        }
        
        private void addField(AuctionEvent event, String element) {
            String[] pair = element.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }
        
        public PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }
        
        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            
            for (String element : fieldsIn(messageBody)) {
                event.addField(event, element);
            }
            
            return event;
        }
        
        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
    }
    
    private final String               sniperId;
    private final AuctionEventListener listener;
    
    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }
    
    public void processMessage(Chat chat, Message message) {
        try {
            translate(message);
        } catch (Exception parseException) {
            listener.auctionFailed();
        }
    }
    
    private void translate(Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());
        
        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }
}