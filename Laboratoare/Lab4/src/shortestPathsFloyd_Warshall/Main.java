package shortestPathsFloyd_Warshall;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class MyThread extends Thread {
    public int id;
    public static int[][] graph;
    public static int[][] previous;
    public static int N;
    private final int start;

    public MyThread(int id) {
        this.id = id;
        start = (int) (Math.ceil((float) N/Main.tcount) * id);
    }

    public void run() {
        for (int k = 0; k < N; k++) {
            for (int i = start; i < N; i++) {
                for (int j = start; j < N; j++) {
                    graph[i][j] = Math.min(previous[i][k] + previous[k][j], previous[i][j]);
                }
            }
        }
        try {
            Main.barrier.await();
            if (id == 0) {
                previous = graph.clone();
            }
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

public class Main {

    public static int tcount = Runtime.getRuntime().availableProcessors();
    public static CyclicBarrier barrier = new CyclicBarrier(tcount);

    public static void main(String[] args) {
        int M = 9;
        int[][] graph = {{0, 1, M, M, M},
            {1, 0, 1, M, M},
            {M, 1, 0, 1, 1},
            {M, M, 1, 0, M},
            {M, M, 1, M, 0}};

        int[][] graph2 = graph.clone();
        Thread[] t = new Thread[tcount];

        // Set static variables to avoid duplicate operations
        MyThread.graph = graph;
        MyThread.previous = graph.clone();
        MyThread.N = graph[0].length;

        for (int i = 0; i < tcount; ++i) {
            t[i] = new MyThread(i);
            t[i].start();
        }

        for (int i = 0; i < tcount; ++i) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("======PARALLEL======\n");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();


        // Parallelize me (You might want to keep the original code in order to compare)
        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    graph2[i][j] = Math.min(graph2[i][k] + graph2[k][j], graph2[i][j]);
                }
            }
        }

        System.out.println("======SEQUENTIAL======\n");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }
    }
}
