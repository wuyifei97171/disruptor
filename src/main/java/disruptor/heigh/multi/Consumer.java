package disruptor.heigh.multi;

import com.lmax.disruptor.WorkHandler;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// 注意 多消费者模型 一定要实现WorkHandler这个接口
public class Consumer implements WorkHandler<Order> {

    private String consumerId;

    private static AtomicInteger count = new AtomicInteger(0);

    private Random random = new Random();

    public Consumer(String consumerId){
        this.consumerId = consumerId;
    }

    public int getCount(){
        return count.get();
    }

    @Override
    public void onEvent(Order event) throws Exception {
        Thread.sleep(1 * random.nextInt(5));
        System.err.println("当前消费者： " + this.consumerId + ", 消费信息："+ event.getId());
        count.incrementAndGet();
    }
}
