package task7;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import util.BSTOperations;
import util.BinarySearchTreeNode;

public class Main {
    private static <T extends Comparable<T>> void searchValue(BinarySearchTreeNode<T> binarySearchTree, T value) {
        if (binarySearchTree != null) {
            if (value.equals(binarySearchTree.getValue())) {
                System.out.println("Found value: " + binarySearchTree.getValue());
            } else if (binarySearchTree.getValue().compareTo(value) > 0) {
                searchValue(binarySearchTree.getLeft(), value);
            } else {
                searchValue(binarySearchTree.getRight(), value);
            }
        }
    }

    public static final int P = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) {
        System.out.println("------------------------Sequential------------------------");
        BinarySearchTreeNode<Integer> binarySearchTree = new BinarySearchTreeNode<>(3);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 6);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 9);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 2);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 10);
        binarySearchTree = BSTOperations.insertNode(binarySearchTree, 1);

        int value = 10;
        searchValue(binarySearchTree, value);

        System.out.println("-------------------------Parallel-------------------------");
        ExecutorService tpe = Executors.newFixedThreadPool(P);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        AtomicInteger counter = new AtomicInteger(1);
        tpe.submit(new MyRunnable(tpe, binarySearchTree, counter, completableFuture, value));
        String result = null;

        try {
            result = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (result != null) {
            System.out.println("Found value: " + result);
        } else {
            System.out.println("Value was not found");
        }
    }
}
