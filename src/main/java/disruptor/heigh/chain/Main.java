package disruptor.heigh.chain;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        // 构建一个线程池用于提交任务 es1
        ExecutorService es1 = Executors.newFixedThreadPool(4);
        // 注意，es作为disruptor的线程池，由于要监听五个线程 h1 ... h5 所以线程池需要5
        ExecutorService es2 = Executors.newFixedThreadPool(5);

        // 1. 构建 Disruptor
        Disruptor<Trade> disruptor = new Disruptor<Trade>(
            new EventFactory<Trade>(){
                @Override
                public Trade newInstance() {
                    return new Trade();
                }
            },
            1024*1024,
             es2,
             ProducerType.SINGLE,
             new BusySpinWaitStrategy()
        );

        // 2. 吧消费者设置到Disruptor中 handleEventsWith
        /**
        // 2.1 串行操作：
        disruptor
                .handleEventsWith(new Handler1())
                .handleEventsWith(new Handler2())
                .handleEventsWith(new Handler3());
        **/

        //2.2 并行操作 可以用两种方式去进行
        /**
        disruptor.handleEventsWith(new Handler1(), new Handler2(), new Handler3());
//        disruptor.handleEventsWith(new Handler1());
//        disruptor.handleEventsWith(new Handler2());
//        disruptor.handleEventsWith(new Handler3());
        */

        // 2.3 菱形操作(1）
        /**
        disruptor.handleEventsWith(new Handler1(), new Handler2())
                .handleEventsWith(new Handler3());
        */
        // 2.3菱形操作(2)
        /**
        EventHandlerGroup<Trade> eventHandlerGroup =
                disruptor.handleEventsWith(new Handler1(), new Handler2());
        eventHandlerGroup.then(new Handler3());
        */

        //2.4 六边形操作
        Handler1 h1 = new Handler1();
        Handler2 h2 = new Handler2();
        Handler3 h3 = new Handler3();
        Handler4 h4 = new Handler4();
        Handler5 h5 = new Handler5();

        disruptor.handleEventsWith(h1,h4);
        disruptor.after(h1).handleEventsWith(h2);
        disruptor.after(h4).handleEventsWith(h5);
        disruptor.after(h2, h5).handleEventsWith(h3);

        // 3. 启动disruptor
        RingBuffer<Trade> ringBuffer = disruptor.start();

        CountDownLatch latch = new CountDownLatch(1);

        long begin = System.currentTimeMillis();

        es1.submit(new TradePushlisher(latch,disruptor));

        latch.await();     // 进行向下

        // 关闭 disruptro 和 ExecutorService
        disruptor.shutdown();
        es1.shutdown();
        es2.shutdown();

        System.err.println("总耗时：" + (System.currentTimeMillis() - begin));
    }
}
