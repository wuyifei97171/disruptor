package disruptor.quickstart;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;

public class Main {

    public static void main(String[] args) {

        // 1.参数准备工作
        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 1024 * 1024;
        ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /**
         * 1.orderEventFactory： 消息(event)工场对象
         * 2.ringBufferSize：    容器长度
         * 3.executor：          线程池（建议使用自定义线程池） RejectedExecutionHandlert
         * 4.ProducerType:       单生产者 还是 多生产者
         * 5.waitStrategy:       等待策略
         */
        // 2. 实例化disruptor对象
        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                orderEventFactory,
                ringBufferSize,
                executor,
                ProducerType.SINGLE,
                new BlockingWaitStrategy()    // 等待策略
        );


    }
}
