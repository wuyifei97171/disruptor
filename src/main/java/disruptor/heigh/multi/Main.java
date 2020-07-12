package disruptor.heigh.multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // 1. 创建RingBuffer
        RingBuffer<Order> ringBuffer =
                RingBuffer.create(
                        ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },
                        1024 * 1024,
                        new YieldingWaitStrategy()
                );

        // 2. 通过RingBuffer 创建一个屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 3. 创建多消费者数组
        Consumer[] consumers = new Consumer[10];
        for(int i = 0; i < consumers.length; i++){
            consumers[i] = new Consumer("C"+ i );
        }

        // 4. 构建多消费者工作池
        WorkerPool<Order> workerPool = new WorkerPool<Order>(
            ringBuffer,
            sequenceBarrier,
            new EventExceptionHandler(),
            consumers
        );

        // 5. 设置多个消费者的 sequence 序号，用于单独统计消费进度,并且设置到ringbuffer中
        ringBuffer.addGatingSequences(
                workerPool.getWorkerSequences()); //获得消费者每一个sequence

        // 6. 启动workPoll,在实际生产中 ThreadPool 需要设置好。 这里设置成5个线程
        workerPool.start(
                Executors.newFixedThreadPool(5));

        final CountDownLatch latch = new CountDownLatch(1);

        // 模拟100个生产者
        for(int i = 0; i < 100; i++){
            final Producer producer = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        latch.await();  // 每个生产者运行之后，就等着
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    for(int j =0;j<100;j++){
                        String uuid = UUID.randomUUID().toString();
                        producer.sendData(uuid);
                    }
                }
            }).start();
        }

        Thread.sleep(2000);
        System.err.println("---------线程创建完毕，开始生产数据---------");
        latch.countDown();

        Thread.sleep(10000);

        System.err.println("任务总数："+consumers[2].getCount());

    }

    static class EventExceptionHandler implements ExceptionHandler<Order>{

        // 过程中发生异常
        @Override
        public void handleEventException(Throwable ex, long sequence, Order event) {

        }

        // 过程开始时发生异常
        @Override
        public void handleOnStartException(Throwable ex) {

        }

        // 过程关闭时时发生异常
        @Override
        public void handleOnShutdownException(Throwable ex) {

        }
    }
}
