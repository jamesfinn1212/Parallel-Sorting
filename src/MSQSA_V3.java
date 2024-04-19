import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class MSQSA_V3 {
    // random quick sort partition
    public static int partition(int[] array, int start, int end) {
        if (start < end) {
            // Choose a pivot index randomly within the range of the subarray
            int pivotIndex = ThreadLocalRandom.current().nextInt(start, end + 1);

            // Swap the pivot element with the last element in the subarray
            int temp = array[pivotIndex];
            array[pivotIndex] = array[end];
            array[end] = temp;

            // Use the last element as the pivot
            int pivot = array[end];

            // Initialize the partition index
            int partitionIndex = start;

            // Partition the array around the pivot
            for (int i = start; i < end; i++) {
                if (array[i] <= pivot) {
                    // Swap elements at i and partitionIndex
                    temp = array[i];
                    array[i] = array[partitionIndex];
                    array[partitionIndex] = temp;

                    partitionIndex++;
                }
            }

            // Swap the pivot element with the element at the partition index
            temp = array[partitionIndex];
            array[partitionIndex] = array[end];
            array[end] = temp;

            // Return the partition index
            return partitionIndex;
        }

        return start; // Return start index if the subarray has only one element
    }

    // non recursive version of quicksort
    public static void quickSortNonRecursive(int[] array, int start, int end) {
        if (start < end) {
            // Stack-based implementation of quicksort
            Stack<Integer> stack = new Stack<>();
            stack.push(start);
            stack.push(end);

            while (!stack.isEmpty()) {
                int high = stack.pop();
                int low = stack.pop();
                int pivotIndex = partition(array, low, high);

                if (pivotIndex - 1 > low) {
                    stack.push(low);
                    stack.push(pivotIndex - 1);
                }

                if (pivotIndex + 1 < high) {
                    stack.push(pivotIndex + 1);
                    stack.push(high);
                }
            }
        }
    }

    // Method to merge two sorted subarrays
    public static void merge(int[] array, int start, int mid, int end) {
        int n1 = mid - start + 1;
        int n2 = end - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, start, L, 0, n1);
        for (int j = 0; j < n2; ++j)
            R[j] = array[mid + 1 + j];

        int i = 0, j = 0;
        int k = start;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }

    public static void main(String[] args) {
        int numIntegers = 1000000;
        int numThreads = 4;
        int[] array = new int[numIntegers];

        for (int i = 0; i < numIntegers; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(-20000, 4000000);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * (numIntegers / numThreads);
            final int end = (i == numThreads - 1) ? numIntegers : (i + 1) * (numIntegers / numThreads);

            executor.execute(new MultiThread(array, start, end));
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            // Wait for all tasks to finish
        }

        int interval = numIntegers / numThreads;
        while (interval < numIntegers) {
            for (int i = 0; i < numIntegers; i += interval * 2) {
                if (i + interval < numIntegers) {
                    merge(array, i, i + interval - 1, Math.min(i + 2 * interval - 1, numIntegers - 1));
                }
            }
            interval *= 2;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Sorted Array: " + Arrays.toString(array));
        System.out.println("Running time: " + (endTime - startTime) + " ms");
        System.out.println(checkCorrect(array));
    }

    public static boolean checkCorrect(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                System.out.println("i " + array[i]);
                System.out.println("i+1 " + array[i + 1]);
                return false;
            }
        }
        return true;
    }

    private static class MultiThread implements Runnable {
        private final int[] array;
        private final int start;
        private final int end;

        public MultiThread(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            quickSortNonRecursive(array, start, end - 1);
        }
    }
}