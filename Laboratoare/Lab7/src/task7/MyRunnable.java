package task7;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import util.BinarySearchTreeNode;

public class MyRunnable implements Runnable {

  private final ExecutorService tpe;
  private final AtomicInteger counter;
  private final int value;
  private final CompletableFuture<String> completableFuture;
  BinarySearchTreeNode<Integer> binarySearchTree;

  public MyRunnable(ExecutorService tpe, BinarySearchTreeNode<Integer> binarySearchTree,
      AtomicInteger counter, CompletableFuture<String> completableFuture, int value) {
    this.tpe = tpe;
    this.counter = counter;
    this.completableFuture = completableFuture;
    this.binarySearchTree = binarySearchTree;
    this.value = value;
  }

  @Override
  public void run() {
    if (binarySearchTree != null) {
      if (value == binarySearchTree.getValue()) {
        completableFuture.complete(binarySearchTree.getValue().toString());
        tpe.shutdown();
      } else if (binarySearchTree.getValue().compareTo(value) > 0) {
        counter.incrementAndGet();
        Runnable t = new MyRunnable(tpe, binarySearchTree.getLeft(), counter, completableFuture,
            value);
        tpe.submit(t);
      } else {
        counter.incrementAndGet();
        Runnable t = new MyRunnable(tpe, binarySearchTree.getRight(), counter, completableFuture,
            value);
        tpe.submit(t);
      }
    }
    int left = counter.decrementAndGet();
    if (left == 0) {
      completableFuture.complete(null);
      tpe.shutdown();
    }
  }
}
