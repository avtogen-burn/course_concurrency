package course.concurrency.m3_shared;

public class Counter {

    private final static Object lock = new Object();
    private static int counter = 1;

    public static void first() {
        while (!Thread.interrupted()) {
            synchronized (lock) {
                while (counter != 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(1);
                counter = 2;
                lock.notify();
            }
        }
    }

    public static void second() {
        while (!Thread.interrupted()) {
            synchronized (lock) {
                while (counter != 2) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(2);
                counter = 3;
                lock.notify();
            }
        }
    }

    public static void third() {
        while (!Thread.interrupted()) {
            synchronized (lock) {
                while (counter != 3) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(3);
                counter = 1;
                lock.notify();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());
        t1.setDaemon(true);
        t2.setDaemon(true);
        t3.setDaemon(true);
        t1.start();
        t2.start();
        t3.start();
        Thread.sleep(1000);

    }
}
