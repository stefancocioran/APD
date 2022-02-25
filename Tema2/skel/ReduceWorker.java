import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class ReduceWorker extends Thread {

	private final int id;
	private final int P;
	private final ArrayList<ReduceTask> tasks;

	public ReduceWorker(int id, ArrayList<ReduceTask> tasks, int P) {
		this.id = id;
		this.P = P;
		this.tasks = tasks;
	}

	// Compute N-th fibonacci number
	public int fibonacci(int n) {
		if (n <= 1) {
			return n;
		} else {
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}

	public void run() {
		int start = (int) (id * ceil((double) tasks.size() / P));
		int end = (int) min((id + 1) * ceil((double) tasks.size() / P), tasks.size());

		for (int i = start; i < end; ++i) {
			double rank = 0;
			long maxLen = 0;
			int maxLenCount = 0;
			int totalWords = 0;

			// Iterate through all the words from lists and find the word of max length
			for (ArrayList<String> list : tasks.get(i).getLongestWordsList()) {
				for (String word : list) {
					if (word.length() > maxLen) {
						maxLen = word.length();
						maxLenCount = 1;
					} else if (word.length() == maxLen) {
						maxLenCount++;
					}
				}
			}

			// Iterate through dictionaries' words and compute rank
			for (Map<Integer, Integer> map : tasks.get(i).getDictionaryList()) {
				for (int key : map.keySet()) {
					totalWords += map.get(key);
					rank += (long) fibonacci(key + 1) * map.get(key);
				}
			}
			tasks.get(i).setRank(rank / totalWords);
			tasks.get(i).setMaxLength(maxLen);
			tasks.get(i).setCount(maxLenCount);
		}
	}
}