import java.util.Arrays;
import java.util.Random;

public class Sort {

    private static final Random PIVOT_RANDOM = new Random();
    private static long comparisonCount;
    private static long moveCount;

    public static void resetCounters() {
        comparisonCount = 0;
        moveCount = 0;
    }

    public static long getComparisonCount() {
        return comparisonCount;
    }

    public static long getMoveCount() {
        return moveCount;
    }

    private static <T extends Comparable<T>> boolean isStrictlyBefore(T firstValue, T secondValue, boolean ascending) {
        comparisonCount++;
        if (ascending) {
            return firstValue.compareTo(secondValue) < 0;
        } else {
            return firstValue.compareTo(secondValue) > 0;
        }
    }

    private static <T extends Comparable<T>> boolean isStrictlyAfter(T firstValue, T secondValue, boolean ascending) {
        comparisonCount++;
        if (ascending) {
            return firstValue.compareTo(secondValue) > 0;
        } else {
            return firstValue.compareTo(secondValue) < 0;
        }
    }

    private static <T extends Comparable<T>> boolean isBeforeOrEqual(T firstValue, T secondValue, boolean ascending) {
        comparisonCount++;
        if (ascending) {
            return firstValue.compareTo(secondValue) <= 0;
        } else {
            return firstValue.compareTo(secondValue) >= 0;
        }
    }

    public static <T> String listToString(T[] values) {
        return listToString(values, 0, values.length - 1);
    }

    public static <T> String listToString(T[] values, int from, int to) {
        StringBuilder result = new StringBuilder();
        for (int index = from; index <= to; index++) {
            result.append(values[index]);
            if (index < to) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private static <T> void swapElements(T[] values, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }
        T temporary = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        moveCount++;
        values[secondIndex] = temporary;
        moveCount++;
    }

    // Insertion Sort
    public static <T extends Comparable<T>> void insertionSort(T[] values, boolean ascending) {
        SortRecorder.capture(values, "Starting array");

        for (int nextIndex = 1; nextIndex < values.length; nextIndex++) {
            T key = values[nextIndex];
            int shiftIndex = nextIndex - 1;

            while (shiftIndex >= 0 && isStrictlyAfter(values[shiftIndex], key, ascending)) {
                values[shiftIndex + 1] = values[shiftIndex];
                moveCount++;
                shiftIndex--;
            }

            values[shiftIndex + 1] = key;
            moveCount++;

            SortRecorder.markPlaced(shiftIndex + 1);
            SortRecorder.markSortedPrefix(nextIndex);
            SortRecorder.capture(values, "Inserted " + key + " into the sorted part");
        }
        SortRecorder.markSortedPrefix(values.length - 1);
        SortRecorder.capture(values, "Sort complete.");
    }

    // Selection Sort
    public static <T extends Comparable<T>> void selectionSort(T[] values, boolean ascending) {
        SortRecorder.capture(values, "Starting array");

        for (int position = 0; position < values.length - 1; position++) {
            int bestIndex = position;

            for (int candidateIndex = position + 1; candidateIndex < values.length; candidateIndex++) {
                if (isStrictlyAfter(values[bestIndex], values[candidateIndex], ascending)) {
                    bestIndex = candidateIndex;
                }
            }

            if (bestIndex != position) {
                SortRecorder.markPair(position, bestIndex);
                T bestValue = values[bestIndex];
                swapElements(values, position, bestIndex);
                SortRecorder.markSortedPrefix(position);
                SortRecorder.capture(values, "Moved " + bestValue + " into position " + position);
            } else {
                SortRecorder.markPlaced(position);
                SortRecorder.markSortedPrefix(position);
                SortRecorder.capture(values, values[position] + " is already in position " + position);
            }
        }
        SortRecorder.markSortedPrefix(values.length - 1);
        SortRecorder.capture(values, "Sort complete.");
    }

    // Merge Sort
    public static <T extends Comparable<T>> void mergeSort(T[] values, boolean ascending) {
        SortRecorder.capture(values, "Starting array");
        mergeSort(values, 0, values.length - 1, ascending);
        SortRecorder.markSortedPrefix(values.length - 1);
        SortRecorder.capture(values, "Sort complete!");
    }

    private static <T extends Comparable<T>> void mergeSort(T[] values, int low, int high, boolean ascending) {
        if (low >= high) {
            return;
        }

        int middle = (low + high) / 2;

        SortRecorder.markRegion(low, high);
        SortRecorder.capture(values, "Splitting range " + low + ".." + high + " at middle " + middle);

        mergeSort(values, low, middle, ascending);
        mergeSort(values, middle + 1, high, ascending);

        mergeHalves(values, low, middle, high, ascending);

        SortRecorder.markRegion(low, high);
        SortRecorder.capture(values, "Merged range " + low + ".." + high);
    }

    private static <T extends Comparable<T>> void mergeHalves(T[] values, int low, int middle, int high,
            boolean ascending) {
        T[] leftHalf = Arrays.copyOfRange(values, low, middle + 1);
        T[] rightHalf = Arrays.copyOfRange(values, middle + 1, high + 1);

        int leftIndex = 0;
        int rightIndex = 0;
        int targetIndex = low;

        while (leftIndex < leftHalf.length && rightIndex < rightHalf.length) {
            if (isBeforeOrEqual(leftHalf[leftIndex], rightHalf[rightIndex], ascending)) {
                values[targetIndex] = leftHalf[leftIndex];
                leftIndex++;
            } else {
                values[targetIndex] = rightHalf[rightIndex];
                rightIndex++;
            }
            moveCount++;
            targetIndex++;
        }

        while (leftIndex < leftHalf.length) {
            values[targetIndex] = leftHalf[leftIndex];
            leftIndex++;
            moveCount++;
            targetIndex++;
        }

        while (rightIndex < rightHalf.length) {
            values[targetIndex] = rightHalf[rightIndex];
            rightIndex++;
            moveCount++;
            targetIndex++;
        }
    }

    // Quick Sort
    public static <T extends Comparable<T>> void quickSort(T[] values, boolean ascending) {
        SortRecorder.capture(values, "Starting array");
        quickSortRange(values, 0, values.length - 1, ascending);
        SortRecorder.markSortedPrefix(values.length - 1);
        SortRecorder.capture(values, "Sort complete!");
    }

    private static <T extends Comparable<T>> void quickSortRange(T[] values, int low, int high, boolean ascending) {
        if (low >= high) {
            if (low == high) {
                SortRecorder.markFinal(low);
                SortRecorder.capture(values, "Single element locked at index " + low);
            }
            return;
        }

        int randomIndex = low + PIVOT_RANDOM.nextInt(high - low + 1);
        swapElements(values, randomIndex, high);

        T pivot = values[high];

        SortRecorder.markRegion(low, high);
        SortRecorder.capture(values, "Partitioning range " + low + ".." + high + " around pivot " + pivot);

        int pivotIndex = partition(values, low, high, ascending);

        SortRecorder.markFinal(pivotIndex);
        SortRecorder.capture(values, "Pivot " + pivot + " locked into final place at index " + pivotIndex);

        quickSortRange(values, low, pivotIndex - 1, ascending);
        quickSortRange(values, pivotIndex + 1, high, ascending);
    }

    private static <T extends Comparable<T>> int partition(T[] values, int low, int high, boolean ascending) {
        T pivot = values[high];
        int lastSmallerIndex = low - 1;

        for (int candidateIndex = low; candidateIndex < high; candidateIndex++) {
            if (isStrictlyBefore(values[candidateIndex], pivot, ascending)) {
                lastSmallerIndex++;
                swapElements(values, lastSmallerIndex, candidateIndex);
            }
        }

        swapElements(values, lastSmallerIndex + 1, high);
        return lastSmallerIndex + 1;
    }
}