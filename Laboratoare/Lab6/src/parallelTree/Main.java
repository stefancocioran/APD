package parallelTree;

import java.util.concurrent.CyclicBarrier;

public class Main {
	static int N_ITERATIONS = 100;

	public static void main(String[] args) {
		Thread[] threads = new Thread[3];
		CyclicBarrier cyclicBarrier;
		System.out.println("Parallel tree problem");
		
		for (int j = 0; j < N_ITERATIONS; j++) {
			TreeNode tree = new TreeNode(1);
			cyclicBarrier = new CyclicBarrier(3);

			threads[0] = new Thread(new ReadTreePart(tree, "treePart1.txt", cyclicBarrier));
			threads[1] = new Thread(new ReadTreePart(tree, "treePart2.txt", cyclicBarrier));
			threads[2] = new Thread(new VerifyTree(tree, cyclicBarrier));
			for (int i = 0; i < 3; i++) {
				threads[i].start();
			}

			for (int i = 0; i < 3; i++) {
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}