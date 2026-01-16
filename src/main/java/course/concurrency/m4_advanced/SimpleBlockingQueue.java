package course.concurrency.m4_advanced;

import java.util.LinkedList;

public class SimpleBlockingQueue<T> {

    private final int capacity;
    private final LinkedList<T> queue = new LinkedList<>();
    private int size = 0;
    private final Object lock = new Object();

    SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(T value) {
        synchronized (lock) {
            while (size == capacity) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.add(value);
            ++size;
            lock.notifyAll();
        }
    }

    public T dequeue() {
        synchronized (lock) {
            while (size == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T result = queue.removeFirst();
            --size;
            lock.notifyAll();
            return result;
        }
    }

    public int getSize() {
        synchronized (lock) {
            return size;
        }
    }
}
