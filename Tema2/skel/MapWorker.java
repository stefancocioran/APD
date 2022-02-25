import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class MapWorker extends Thread {

	private final int id;
	private final int P;
	private final ArrayList<MapTask> tasks;
	private final Map<MapTask, Map<Integer, Integer>> dictionary;
	private final Map<MapTask, ArrayList<String>> longestWords;

	private ArrayList<String> taskLongestWords;
	private Map<Integer, Integer> taskDictionary;
	private int wordMaxSize;

	public MapWorker(int id, ArrayList<MapTask> tasks, int P) {
		this.id = id;
		this.P = P;
		this.tasks = tasks;
		this.wordMaxSize = 0;
		this.dictionary = new HashMap<>();
		this.longestWords = new HashMap<>();
	}

	public void run() {
		int start = (int) (id * ceil((double) tasks.size() / P));
		int end = (int) min((id + 1) * ceil((double) tasks.size() / P), tasks.size());

		for (int i = start; i < end; ++i) {
			try {
				wordMaxSize = 0;
				taskLongestWords = new ArrayList<>();
				taskDictionary = new HashMap<>();

				StringBuilder fragment = new StringBuilder();
				RandomAccessFile raf = new RandomAccessFile(tasks.get(i).getDocName(), "r");
				char chr = 0;
				boolean splitWord = false;
				// If it is the first fragment of the document, set the cursor at the very beginning
				if (tasks.get(i).getOffset() == 0) {
					raf.seek(0);
				} else {
					// Set the cursor at one position before the computed offset
					// to check if the current word was split
					raf.seek(tasks.get(i).getOffset() - 1);
					chr = (char) raf.readByte();
					// If the first character is not a delimiter, it means that
					// the word was split
					if (Character.isLetter(chr) || Character.isDigit(chr)) {
						splitWord = true;
					}
				}

				int count = 0;
				boolean eof = false;
				if (splitWord) {
					// If the word was split, read characters until it meets a delimiter
					while (!eof) {
						try {
							chr = (char) raf.readByte();
							count++;
							if (!(Character.isLetter(chr) || Character.isDigit(chr))) {
								break;
							}
						} catch (EOFException e) {
							eof = true;
						}
					}
				}

				// Read what is left from fragment
				// Anytime it encounters a delimiter, a complete word was found
				for (int j = count; j < tasks.get(i).getFragmentSize(); ++j) {
					chr = (char) raf.readByte();
					if (Character.isLetter(chr) || Character.isDigit(chr)) {
						fragment.append(chr);
					} else {
						addWord(fragment);
					}
				}

				// If the last read character is a delimiter it means that
				// the last word in the fragment is complete
				if (!(Character.isLetter(chr) || Character.isDigit(chr)) || eof) {
					raf.close();
					addToMap(i);
					continue;
				}

				// If the last read character is NOT a delimiter it means that
				// the last word in the fragment might have missing characters
				while (!eof) {
					// Read characters until it meets a delimiter or EOF
					// If it encounters a delimiter or EOF, the complete word was found
					try {
						chr = (char) raf.readByte();
						if (Character.isLetter(chr) || Character.isDigit(chr)) {
							fragment.append(chr);
						} else {
							addWord(fragment);
							break;
						}
					} catch (EOFException e) {
						addWord(fragment);
						eof = true;
					}
				}

				addToMap(i);
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addWord(StringBuilder fragment) {
		if (fragment.length() >= wordMaxSize) {
			wordMaxSize = fragment.length();
			taskLongestWords.add(String.valueOf(fragment));
		}

		if (fragment.length() != 0) {
			if (taskDictionary.containsKey(fragment.length())) {
				taskDictionary.put(fragment.length(), taskDictionary.get(fragment.length()) + 1);
			} else {
				taskDictionary.put(fragment.length(), 1);
			}
		}
		fragment.setLength(0);
	}

	private void addToMap(int i) {
		// Remove the words that are not the longest anymore
		taskLongestWords.removeIf(item -> item.length() < wordMaxSize);
		dictionary.put(tasks.get(i), taskDictionary);
		longestWords.put(tasks.get(i), taskLongestWords);
	}

	public Map<MapTask, ArrayList<String>> getLongestWords() {
		return longestWords;
	}

	public Map<MapTask, Map<Integer, Integer>> getDictionary() {
		return dictionary;
	}
}
