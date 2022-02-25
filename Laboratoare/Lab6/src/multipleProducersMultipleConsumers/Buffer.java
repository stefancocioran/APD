package multipleProducersMultipleConsumers;

import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
	int value;
	static int BUFFER_CAPACITY = 5;
	ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue(BUFFER_CAPACITY, true);

	void put(int value) {
		try {
			queue.put(value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int get() {
		try {
			value = queue.take();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return value;
	}
}
