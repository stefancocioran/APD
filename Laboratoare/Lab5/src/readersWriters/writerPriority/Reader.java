package readersWriters.writerPriority;

import java.util.concurrent.BrokenBarrierException;

public class Reader extends Thread {
    private final int id;

    public Reader(int id) {
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

            if (Main.currentWriters > 0 || Main.waitingWriters > 0) {
                Main.waitingReaders++;
                Main.enter.release();
                try {
                    Main.readSem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Main.currentReaders++;
            // TODO
            if (Main.waitingReaders > 0) {
                Main.waitingReaders--;
                Main.readSem.release();
            } else if (Main.waitingReaders == 0) {
                Main.enter.release();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Reader " + id + " is reading");
            Main.hasRead[id] = true;

            // TODO
            try {
                Main.enter.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Main.currentReaders--;

            // TODO
            if (Main.currentReaders == 0 && Main.waitingWriters > 0) {
                Main.waitingWriters--;
                Main.writeSem.release();
            } else if (Main.currentReaders > 0 || Main.waitingWriters == 0) {
                Main.enter.release();
            }

        } while (!Main.hasRead[id]);
    }
}
