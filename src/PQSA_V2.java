
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

public class PQSA_V2 {
    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;

        public QuickSortTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (start < end) {
                int pivotIndex = partition(array, start, end);
                QuickSortTask left = new QuickSortTask(array, start, pivotIndex - 1);
                QuickSortTask right = new QuickSortTask(array, pivotIndex + 1, end);
                invokeAll(left, right);
            }
        }
    }

    public static void main(String[] args) {
        int numInteger = 1000000;
        int[] array = new int[numInteger];

        // Generate random integers
        for (int i = 0; i < numInteger; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(0, 40000);
        }

        // Record start time
        long startTime = System.currentTimeMillis();

        // Specify the parallelism level
        int numThreads = 4; // Example: use 4 threads
        ForkJoinPool pool = new ForkJoinPool(numThreads);

        // Create the QuickSortTask with the custom ForkJoinPool
        QuickSortTask task = new QuickSortTask(array, 0, array.length - 1);

        // Invoke the task
        pool.invoke(task);

        // Record end time
        long endTime = System.currentTimeMillis();

        // Check correctness and measure time
        System.out.println("Sorted Array: " + Arrays.toString(array));
        System.out.println("Running time: " + (endTime - startTime) + " ms");
        System.out.println(checkCorrect(array));
    }

    public static int partition(int[] array, int start, int end) {
        if (start < end) {
            int pivotIndex = ThreadLocalRandom.current().nextInt(start, end + 1);
            int temp = array[pivotIndex];
            array[pivotIndex] = array[end];
            array[end] = temp;
            int pivot = array[end];
            int partitionIndex = start;
            for (int i = start; i < end; i++) {
                if (array[i] <= pivot) {
                    temp = array[i];
                    array[i] = array[partitionIndex];
                    array[partitionIndex] = temp;
                    partitionIndex++;
                }
            }
            temp = array[partitionIndex];
            array[partitionIndex] = array[end];
            array[end] = temp;
            return partitionIndex;
        }
        return start;
    }

    public static boolean checkCorrect(int[] array){
        for(int i = 0; i< array.length-1; i++){
            if(array[i] > array[i+1]) {
                System.out.println("i " + array[i]);
                System.out.println("i+1 " + array[i+1]);
                return false;
            }
        }
        return true;
    }
}