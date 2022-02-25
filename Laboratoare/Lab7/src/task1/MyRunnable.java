package task1;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
	private final ArrayList<Integer> partialPath;
	private final ExecutorService tpe;
	private final int targetNode;
	AtomicInteger shutdownCount;

	public MyRunnable(ArrayList<Integer> partialPath, ExecutorService tpe, int targetNode,
			AtomicInteger shutdownCount) {
		this.partialPath = partialPath;
		this.tpe = tpe;
		this.targetNode =  targetNode;
		this.shutdownCount = shutdownCount;
	}


	@Override
	public void run() {
		if (partialPath.get(partialPath.size() - 1) == targetNode) {
			System.out.println(partialPath);
		}

		int lastNodeInPath = partialPath.get(partialPath.size() - 1);
		for (int[]ints : Main.graph) {
			// connection node
			if (ints[0] == lastNodeInPath && !partialPath.contains(ints[1])) {
				if (partialPath.contains(ints[1]))
					continue;
				ArrayList<Integer> newPartialPath = new ArrayList<>(partialPath);
				newPartialPath.add(ints[1]);
				shutdownCount.incrementAndGet();
				tpe.submit(new MyRunnable(newPartialPath, tpe, targetNode, shutdownCount));
			}
		}

		shutdownCount.decrementAndGet();
		if (shutdownCount.get() == 0) {
			tpe.shutdown();
		}
	}
}
