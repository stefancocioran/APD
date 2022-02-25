package doubleVectorElements;

public class MyThread implements Runnable {
    private final int id;
    private final int N;
    private int[]v;
    private final int start;
    private final int end;

    public MyThread(int id, int N, int[] v){
        this.id = id;
        this.N = N;
        this.v = v;
        start = (int) (id * Math.ceil((double)N/Main.P));
        end = (int) Math.min((id + 1) * Math.ceil((double)N/Main.P), N);
    }


    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            v[i] = v[i] * 2;
        }
    }
}
