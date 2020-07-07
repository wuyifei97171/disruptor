package disruptor.quickstart;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        // 1. 实例化disruptor对象

        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 1024 * 1024;
        ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                orderEventFactory,
                ringBufferSize,
                executor,
                producerType,
                waitStrategy
        );
    }
}
