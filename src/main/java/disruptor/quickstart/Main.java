package disruptor.quickstart;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;

public class Main {

    public static void main(String[] args) {
        /**
         * LinkedBlockingQueue：可以实现有界队列也能实现无界队列
         * 优点：其内部在实现put、take操作的时候分别使用了两个显式锁(putLock和takeLock)，降低了锁争用的可能性。
         * 缺点：其内部存储空间是一个链表，put、take操作都会导致链表节点的动态创建和移除，这可能增加垃圾回收的负担。
         * put、take操作使用的是两个锁，它维护队列当前长度size时无法使用一个普通的int型变量而是使用一个原子变量，
         * 这个原子变量可能会被生产者线程和消费者线程争用，导致额外开销。
         * LinkedBlockingQueue 适合在生产者线程和消费者线程之间的并发程度比较大的情况下使用。
         */
        // 1.参数准备工作
        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 1024 * 1024;
        ExecutorService executor =
                // 此处使用的是默认线程池，在生产环境中需要使用自定义线程池，规定线程数量
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

        // 3.添加消费者的监听（用handleEventsWith 将消费者和disruptor进行关联）
        disruptor.handleEventsWith(new OrderEventHandler());

        // 4.启动disruptor
        disruptor.start();

        // 5.获取【实际存储数据】的容器: RingBuffer
        RingBuffer<OrderEvent> ringBuffer= disruptor.getRingBuffer();

        OrderEventProducer producer = new OrderEventProducer(ringBuffer);

        // 初始化 8个长度
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        for(long i = 0 ; i<100;i++){
            byteBuffer.putLong(0 , i);
            producer.sendData(byteBuffer);
        }

        // 关闭
        disruptor.shutdown();
        executor.shutdown();
    }

}
