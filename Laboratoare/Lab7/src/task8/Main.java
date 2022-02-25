package task8;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import util.BSTOperations;
import util.BinarySearchTreeNode;

public class Main {
    private static <T extends Comparable<T>> int calculateMaximumHeight(BinarySearchTreeNode<T> binarySearchTree) {
        if (binarySearchTree == null) {
            return 0;
        }

        return 1 + Math.max(
                calculateMaximumHeight(binarySearchTree.getRight()),
                calculateMaximumHeight(binarySearchTree.getLeft())
        );
    }
    public static final int P = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("------------------------Sequential------------------------");
        BinarySearchTreeNode<Integer> binarySearchTree = new BinarySearchTreeNode<>(3);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 6);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 9);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 2);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 10);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 1);

        System.out.println(calculateMaximumHeight(binarySearchTree));

        System.out.println("-------------------------Parallel-------------------------");
        ForkJoinPool forkJoinPool = new ForkJoinPool(P);
        MyRunnable calculator = new MyRunnable(binarySearchTree);
        forkJoinPool.execute(calculator);
        System.out.println(calculator.get());
    }
}
