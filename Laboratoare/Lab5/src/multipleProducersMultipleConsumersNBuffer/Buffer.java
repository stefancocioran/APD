package multipleProducersMultipleConsumersNBuffer;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {

  Queue<Integer> queue;
  Semaphore semFull;
  Semaphore semEmpty;

  public Buffer(int size) {

    queue = new LimitedQueue<>(size);
    semFull = new Semaphore(0);
    semEmpty = new Semaphore(size);
  }

  void put(int value) {
    try {
      semEmpty.acquire();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }

    synchronized (this) {
      queue.add(value);
    }

    semFull.release();
  }

  public int get() {
    try {
      semFull.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int a = -1;
    synchronized (this) {
      Integer result = queue.poll();
      if (result != null) {
        a = result;
      }
    }
    semEmpty.release();
    return a;
  }
}