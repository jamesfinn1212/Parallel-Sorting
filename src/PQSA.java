import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PQSA {
    public static void main(String[] args) {
        // Number of elements in the array
        int size = 5000000;
        // Number of processors available to the JVM
        int numProcessors = 4;

        // Create an integer array with random values
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * 1000000);
        }

        // Create a ForkJoinPool with the number of available processors
        ForkJoinPool pool = new ForkJoinPool(numProcessors);

        // Create a task to sort the entire array
        SortTask task = new SortTask(data, 0, data.length - 1);

        // Start the timer
        long startTime = System.currentTimeMillis();

        // invoke the task (start the quick sort)
        pool.invoke(task);

        // Stop the timer
        long endTime = System.currentTimeMillis();

        // Print the time taken to sort the array
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    // Task to sort a subarray of an integer array using Quick Sort
    static class SortTask extends RecursiveAction {
        private static final int THRESHOLD = 1000; // the size of array to perform regular quicksort on. Too low = resources spent on overhead, too high = benefits of parallel programming taper off
        private int[] data; // array to sort
        private int low; // lower-bound index from which subarray starts
        private int high; // higher-bound index from which subarray ends

        // constructor
        SortTask(int[] data, int low, int high) {
            this.data = data;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low < high) {
                if (high - low <= THRESHOLD) {
                    // If the size of the subarray is below the threshold, perform sequential quicksort
                    sequentialQuickSort(data, low, high);
                } else {
                    // Otherwise, continue with parallel quicksort
                    int pivotIndex = partition(data, low, high);

                    // split array on random partition and invoke both processes at once
                    SortTask left = new SortTask(data, low, pivotIndex - 1);
                    SortTask right = new SortTask(data, pivotIndex + 1, high);
                    invokeAll(left, right);
                }
            }
        }

        // Partition the array and return the pivot index
        private int partition(int[] arr, int low, int high) {
            int pivotIndex = low + (int) (Math.random() * (high - low + 1)); // choose random pivot
            int pivot = arr[pivotIndex];
            swap(arr, pivotIndex, high);
            int i = low;
            for (int j = low; j < high; j++) {
                if (arr[j] < pivot) {
                    swap(arr, i, j);
                    i++;
                }
            }
            swap(arr, i, high);
            return i;
        }

        // Sequential QuickSort algorithm
        private void sequentialQuickSort(int[] arr, int low, int high) {
            if (low < high) {
                int pi = partition(arr, low, high);
                sequentialQuickSort(arr, low, pi - 1);
                sequentialQuickSort(arr, pi + 1, high);
            }
        }

        // Utility method to swap two elements in an array
        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
