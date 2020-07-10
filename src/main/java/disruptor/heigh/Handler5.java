package disruptor.heigh;

import com.lmax.disruptor.EventHandler;

public class Handler5 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        System.err.println("handler 5 : GET PRICE"
                + event.getPrice());
        event.setPrice(event.getPrice() + 3.0);
    }
}
