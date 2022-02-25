package task3;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
	private final int[] graph;
	private final ExecutorService tpe;
	private final int step;
	AtomicInteger shutdownCount;

	public MyRunnable(int[] graph, ExecutorService tpe, int step,
			AtomicInteger shutdownCount) {
		this.graph = graph;
		this.tpe = tpe;
		this.step = step;
		this.shutdownCount = shutdownCount;
	}

		private static boolean check(int[] arr, int step) {
			for (int i = 0; i <= step; i++) {
				for (int j = i + 1; j <= step; j++) {
					if (arr[i] == arr[j] || arr[i] + i == arr[j] + j || arr[i] + j == arr[j] + i)
						return false;
				}
			}
			return true;
		}

		private static void printQueens(int[] sol) {
			StringBuilder aux = new StringBuilder();
			for (int i = 0; i < sol.length; i++) {
				aux.append("(").append(sol[i] + 1).append(", ").append(i + 1).append("), ");
			}
			aux = new StringBuilder(aux.substring(0, aux.length() - 2));
			System.out.println("[" + aux + "]");
		}

		@Override
		public void run() {
			if (Main.N == step) {
				printQueens(graph);
				shutdownCount.decrementAndGet();
				return;
			}

			for (int i = 0; i < Main.N; ++i) {
				int[] newGraph = graph.clone();
				newGraph[step] = i;

				if (check(newGraph, step)) {
					shutdownCount.incrementAndGet();
					tpe.submit(new MyRunnable(newGraph, tpe,step + 1, shutdownCount));
				}
			}

			shutdownCount.decrementAndGet();
			if (shutdownCount.get() == 0) {
				tpe.shutdown();
			}
		}
}
