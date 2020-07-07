package disruptor.quickstart;

import com.lmax.disruptor.EventHandler;

public class OrderEventHandler implements EventHandler<OrderEvent>{

    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        System.err.println("消费者：" + orderEvent.getValue());
    }

}
