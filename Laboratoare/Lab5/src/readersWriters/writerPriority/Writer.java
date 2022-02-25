package readersWriters.writerPriority;

import java.util.concurrent.BrokenBarrierException;

public class Writer extends Thread {
    private final int id;

    public Writer(int id) {
        super();
        this.id = id;
    }

    @Override
    public void run() {
        try {
            Main.barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        do {
            // TODO
            try {
                Main.enter.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Main.currentReaders > 0 || Main.currentWriters > 0) {
                Main.waitingWriters++;
                Main.enter.release();
                try {
                    Main.writeSem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Main.currentWriters++;

            // TODO

            Main.enter.release();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Writer " + id + " is writing");
            Main.hasWritten[id] = true;

            // TODO
            try {
                Main.enter.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Main.currentWriters--;

            // TODO
            if (Main.waitingReaders > 0 && Main.waitingWriters > 0) {
                Main.waitingReaders--;
                Main.readSem.release();
            } else if (Main.waitingWriters > 0) {
                Main.waitingWriters--;
                Main.writeSem.release();
            } else if (Main.waitingReaders == 0 && Main.waitingWriters == 0) {
                Main.enter.release();
            }

        } while (!Main.hasWritten[id]);
    }
}
