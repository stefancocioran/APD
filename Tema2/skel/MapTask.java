public class MapTask {
	private final String docName;
	private final long offset;
	private final int fragmentSize;

	public MapTask(String docName, long offset, int fragmentSize) {
		this.docName = docName;
		this.offset = offset;
		this.fragmentSize = fragmentSize;
	}

	public int getFragmentSize() {
		return fragmentSize;
	}

	public long getOffset() {
		return offset;
	}

	public String getDocName() {
		return docName;
	}
}
