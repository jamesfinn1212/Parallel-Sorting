import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MSQSA1 {
    // Method to perform non-recursive quick sort on a subarray
    public static void quickSort(int[] array, int start, int end) {
        if (start < end) {
            // Choose a pivot index randomly within the range of the subarray
            int pivotIndex = ThreadLocalRandom.current().nextInt(start, end + 1);
            int pivot = array[pivotIndex];
            int i = start, j = end;
            // Partition the array around the pivot
            while (i <= j) {
                while (array[i] < pivot) {
                    i++;
                }
                while (array[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    i++;
                    j--;
                }
            }
            // Recursively sort the two halves
            if (start < j)
                quickSort(array, start, j);
            if (i < end)
                quickSort(array, i, end);
        }
    }

    // Method to merge two sorted subarrays
    public static void merge(int[] array, int start, int mid, int end) {
        // Find sizes of two subarrays to be merged
        int n1 = mid - start + 1;
        int n2 = end - mid;

        /* Create temp arrays */
        int[] L = new int[n1];
        int[] R = new int[n2];

        /*Copy data to temp arrays*/
        System.arraycopy(array, start, L, 0, n1);
        for (int j = 0; j < n2; ++j)
            R[j] = array[mid + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
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

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }

    public static void main(String[] args) {
        // Input parameters
        int num_integer = 1000000; // Number of random integers
        int num_thread = 4; // Number of threads
        int[] Array = new int[num_integer];

        // Generate N random integers
        for (int i = 0; i < num_integer; i++) {
            Array[i] = ThreadLocalRandom.current().nextInt();
        }

        // Create an array of threads
        Thread[] threads = new Thread[num_thread];

        // Record start time
        long st = System.currentTimeMillis();

        // Start each sorting thread
        for (int i = 0; i < num_thread; i++) {
            final int start = i * (num_integer / num_thread);
            final int end = (i == num_thread - 1) ? num_integer : (i + 1) * (num_integer / num_thread);
            threads[i] = new Thread(() -> {
                quickSort(Array, start, end - 1);
            });
            threads[i].start();
        }

        // Wait for all sorting threads to finish
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Merge the sorted subarrays
        int interval = num_integer / num_thread;
        while (interval < num_integer) {
            for (int i = 0; i < num_integer; i += interval * 2) {
                if (i + interval < num_integer) {
                    merge(Array, i, i + interval - 1, Math.min(i + 2 * interval - 1, num_integer - 1));
                }
            }
            interval *= 2;
        }

        // Record end time and calculate running time
        long et = System.currentTimeMillis();
        System.out.println("Sorted Array: " + Arrays.toString(Array));
        System.out.println("Running time: " + (et - st) + " ms");
    }
}