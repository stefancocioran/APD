package task8;

import java.util.concurrent.RecursiveTask;
import util.BinarySearchTreeNode;

public class MyRunnable extends RecursiveTask<Integer> {
    private final BinarySearchTreeNode<Integer> binarySearchTree;

    public MyRunnable(BinarySearchTreeNode<Integer> binarySearchTree) {
        this.binarySearchTree = binarySearchTree;
    }

    @Override
    protected Integer compute() {
        if (binarySearchTree == null) {
            return 0;
        }
        MyRunnable left = new MyRunnable(binarySearchTree.getLeft());
        MyRunnable right = new MyRunnable( binarySearchTree.getRight());

        left.fork();
        right.fork();

        return 1 + Math.max(left.join(), right.join());
    }
}
