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

        ForkJoinPool pool = ForkJoinPool.commonPool();
        QuickSortTask task = new QuickSortTask(array, 0, array.length - 1);
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

            // Print array for visualization (optional)


            // Return the partition index
            return partitionIndex;
        }

        return start; // Return start index if the subarray has only one element
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

    // Other methods (partition, checkCorrect) remain unchanged
}