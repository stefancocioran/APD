import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReduceTask {
	private final String docName;
	private final List<ArrayList<String>> longestWordsList;
	private final List<Map<Integer, Integer>> dictionaryList;
	private double rank;
	private long maxLength;
	private int count;

	public ReduceTask(String docName, List<Map<Integer, Integer>> dictionaryList,
					  List<ArrayList<String>> longestWordsList) {
		this.docName = docName;
		this.dictionaryList = dictionaryList;
		this.longestWordsList = longestWordsList;
	}

	public String getDocName() {
		return docName;
	}

	public List<ArrayList<String>> getLongestWordsList() {
		return longestWordsList;
	}

	public List<Map<Integer, Integer>> getDictionaryList() {
		return dictionaryList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(long maxLength) {
		this.maxLength = maxLength;
	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}
}
