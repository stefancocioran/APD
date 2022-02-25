package oneProducerOneConsumer;

public class Buffer {
    private int a;
    private volatile boolean full = false;

    public synchronized void put(int value) {
        while (full) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        a = value;
        full = true;
        this.notify();
    }

    public synchronized int get() {
        while (!full) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        full = false;
        this.notify();
        return a;
    }
}
