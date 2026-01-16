package course.concurrency.m4_advanced;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleBlockingQueueTest {

    @Test
    public void orderedEnqDeq() {
        int capacity = 10;
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(capacity);

        for (int i = 0; i < capacity; ++i) {
            queue.enqueue(i);
        }
        assertEquals(capacity, queue.getSize());
        for (int i = 0; i < capacity; ++i) {
            Integer valueFromQ = queue.dequeue();
            assertEquals(i, valueFromQ);
        }
        assertEquals(0, queue.getSize());
    }

    @Test
    public void blocking() throws InterruptedException {
        int capacity = 10;
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(capacity);

        CountDownLatch readUnblocked = new CountDownLatch(1);
        new Thread(() -> { queue.dequeue(); readUnblocked.countDown(); }).start();
        assertEquals(0, queue.getSize());
        queue.enqueue(100);
        readUnblocked.await(30, TimeUnit.SECONDS);
        assertEquals(0, queue.getSize());

        for (int i = 0; i < capacity; ++i) {
            queue.enqueue(i);
        }
        assertEquals(capacity, queue.getSize());

        CountDownLatch writeUnblocked = new CountDownLatch(1);
        new Thread(() -> { queue.enqueue(50); writeUnblocked.countDown(); }).start();
        assertEquals(capacity, queue.getSize());
        queue.dequeue();
        writeUnblocked.await(30, TimeUnit.SECONDS);
        assertEquals(capacity, queue.getSize());
    }
}
