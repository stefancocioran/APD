import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Tema2 {

	public static void main(String[] args) throws IOException {
		int P = 0;
		String inFile;
		String outFile;

		if (args.length < 3) {
			System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
			System.exit(1);
		}
		try {
			P = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("First argument needs to be an integer!");
			System.exit(1);
		}

		inFile = args[1];
		outFile = args[2];

		FileWriter writer = new FileWriter(outFile);
		BufferedReader reader = new BufferedReader(new FileReader(inFile));

		int fragmentSize = Integer.parseInt(reader.readLine());
		int docNumber = Integer.parseInt(reader.readLine());
		ArrayList<String> docs = new ArrayList<>();

		for (int i = 0; i < docNumber; ++i) {
			docs.add(reader.readLine());
		}

		// CreateMapTasks
		ArrayList<MapTask> tasksMap = createMapTasks(docs, fragmentSize);

		Thread[] threadsMap = new Thread[P];
		for (int i = 0; i < P; ++i) {
			threadsMap[i] = new MapWorker(i, tasksMap, P);
			threadsMap[i].start();
		}

		Map<MapTask, Map<Integer, Integer>> dictionaryMapResult = new HashMap<>();
		Map<MapTask, ArrayList<String>> longestWordsMapResult = new HashMap<>();

		for (int i = 0; i < P; ++i) {
			try {
				threadsMap[i].join();
				// Collect Map stage results
				dictionaryMapResult.putAll(((MapWorker) threadsMap[i]).getDictionary());
				longestWordsMapResult.putAll(((MapWorker) threadsMap[i]).getLongestWords());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// CreateReduceTasks
		ArrayList<ReduceTask> tasksReduce = createReduceTasks(docs, dictionaryMapResult, longestWordsMapResult);
		Thread[] threadsReduce = new Thread[P];
		for (int i = 0; i < P; ++i) {
			threadsReduce[i] = new ReduceWorker(i, tasksReduce, P);
			threadsReduce[i].start();
		}

		for (int i = 0; i < P; ++i) {
			try {
				threadsReduce[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Sort tasks by rank in descending order
		tasksReduce.sort((o1, o2) -> Comparator.comparingDouble(ReduceTask::getRank).compare(o2, o1));

		for (ReduceTask task : tasksReduce) {
			String rankString = String.format(",%.2f,", task.getRank());
			String fileName = task.getDocName().replace("tests/files/", "");
			writer.write(fileName + rankString + task.getMaxLength() + "," + task.getCount() + "\n");
		}
		reader.close();
		writer.close();
	}


	public static ArrayList<MapTask> createMapTasks(ArrayList<String> docs, int fragmentSize) {
		ArrayList<MapTask> tasksMap = new ArrayList<>();

		for (String doc : docs) {
			File file = new File(doc);
			long fileSize = file.length();
			long offset = 0;
			while (true) {
				if (fileSize >= fragmentSize) {
					MapTask task = new MapTask(doc, offset, fragmentSize);
					tasksMap.add(task);
					fileSize -= fragmentSize;
					offset += fragmentSize;
				} else {
					MapTask task = new MapTask(doc, offset, (int) fileSize);
					tasksMap.add(task);
					break;
				}
			}
		}
		return tasksMap;
	}


	public static ArrayList<ReduceTask> createReduceTasks(
			ArrayList<String> docs, Map<MapTask,
			Map<Integer, Integer>> dictionaryMapResult,
			Map<MapTask, ArrayList<String>> longestWordsMapResult) {

		ArrayList<ReduceTask> tasksReduce = new ArrayList<>();
		for (String doc : docs) {
			// Group the longest words list and dictionaries for every document
			List<ArrayList<String>> longestWordsList = longestWordsMapResult.entrySet().stream()
					.filter(map -> map.getKey().getDocName().equals(doc)).
					map(Map.Entry::getValue).collect(Collectors.toList());

			List<Map<Integer, Integer>> dictionaryList = dictionaryMapResult.entrySet().stream()
					.filter(map -> map.getKey().getDocName().equals(doc)).
					map(Map.Entry::getValue).collect(Collectors.toList());

			ReduceTask task = new ReduceTask(doc, dictionaryList, longestWordsList);
			tasksReduce.add(task);
		}
		return tasksReduce;
	}
}
