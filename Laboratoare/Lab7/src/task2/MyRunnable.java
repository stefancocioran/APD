package task2;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
	private final int[] colors;
	private final int step;
	private final ExecutorService tpe;
	private final AtomicInteger shutdownCount;

	public MyRunnable(int[] colors, ExecutorService tpe, int step, AtomicInteger shutdownCount) {
		this.colors = colors;
		this.tpe = tpe;
		this.step = step;
		this.shutdownCount = shutdownCount;
	}

	@Override
	public void run() {
		if (step == Main.N) {
			printColors(colors);
			shutdownCount.decrementAndGet();
			return;
		}

		// for the node at position step try all possible colors
		for (int i = 0; i < Main.COLORS; i++) {
			int[] newColors = colors.clone();
			newColors[step] = i;
			if (verifyColors(newColors, step)) {
				shutdownCount.incrementAndGet();
				tpe.submit(new MyRunnable(newColors, tpe,step + 1, shutdownCount));
			}
		}

		shutdownCount.decrementAndGet();
		if (shutdownCount.get() == 0) {
			tpe.shutdown();
		}
	}

	private static boolean verifyColors(int[] colors, int step) {
		for (int i = 0; i < step; i++) {
			if (colors[i] == colors[step] && isEdge(i, step))
				return false;
		}
		return true;
	}

	private static boolean isEdge(int a, int b) {
		for (int[] ints : Main.graph) {
			if (ints[0] == a && ints[1] == b)
				return true;
		}
		return false;
	}

	static void printColors(int[] colors) {
		StringBuilder aux = new StringBuilder();
		for (int color : colors) {
			aux.append(color).append(" ");
		}
		System.out.println(aux);
	}
}
