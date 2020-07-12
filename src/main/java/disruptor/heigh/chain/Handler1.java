package disruptor.heigh.chain;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 使用 EventHandle 和 WorKHandler 都可以对消费端进行监听
 * WorkHandler  相对更简单点，因为参数更少
 */
public class Handler1 implements EventHandler<Trade>, WorkHandler<Trade> {

    // EventHandler
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    // WorkHandler
    @Override
    public void onEvent(Trade event) throws Exception {
        System.err.println("handler 1: SET NAME");
        event.setName("H1");
        Thread.sleep(1000);
    }
}
