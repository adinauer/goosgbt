package at.dinauer.goosgbt;


import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;


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
        
        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }
        
        private String get(String fieldName) {
            return fields.get(fieldName);
        }
        
        private void addField(AuctionEvent event, String element) {
            String[] pair = element.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
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
    
    private final AuctionEventListener listener;
    
    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }
    
    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());
        
        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
            listener.currentPrice(event.currentPrice(), event.increment());
        }
    }
}