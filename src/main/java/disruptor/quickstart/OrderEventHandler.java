package disruptor.quickstart;

import com.lmax.disruptor.EventHandler;

public class OrderEventHandler implements EventHandler<OrderEvent>{

    /**
     * 消费端处理者
     * @param orderEvent
     * @param l
     * @param b
     * @throws Exception
     */
    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        System.err.println("消费者：" + orderEvent.getValue());
    }

}
