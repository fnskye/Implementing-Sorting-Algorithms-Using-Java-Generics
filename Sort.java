import java.util.Arrays;

public class Sort {
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
		if (firstIndex == secondIndex)
			return;
		T temporary = values[firstIndex];
		values[firstIndex] = values[secondIndex];
		moveCount++;
		values[secondIndex] = temporary;
		moveCount++;
	}

	// Insertion Sort
	public static <T extends Comparable<T>> void insertionSort(T[] arr, boolean ascending) {
		SortRecorder.capture(arr, "Starting array");

		for (int i = 1; i < arr.length; i++) {
			T key = arr[i];
			int j = i - 1;

			if (ascending) {
				while (j >= 0 && arr[j].compareTo(key) > 0) {
					comparisonCount++;
					arr[j + 1] = arr[j];
					moveCount++;
					j--;
				}
				if (j >= 0)
					comparisonCount++; // Count final failed check
			} else {
				while (j >= 0 && arr[j].compareTo(key) < 0) {
					comparisonCount++;
					arr[j + 1] = arr[j];
					moveCount++;
					j--;
				}
				if (j >= 0)
					comparisonCount++;
			}

			arr[j + 1] = key;
			moveCount++;

			SortRecorder.markPlaced(j + 1);
			SortRecorder.markSortedPrefix(i);
			SortRecorder.capture(arr, "Inserted " + key + " into the sorted part");
		}

		SortRecorder.markSortedPrefix(arr.length - 1);
		SortRecorder.capture(arr, "Sort complete!");
	}

	// Selection Sort (Antivo)
	public static <T extends Comparable<T>> void selectionSort(T[] arr, boolean ascending) {
		SortRecorder.capture(arr, "Starting array");
		int n = arr.length;

		for (int i = 0; i < n - 1; i++) {
			int selectedIndex = i;

			for (int j = i + 1; j < n; j++) {
				comparisonCount++;
				if (ascending) {
					if (arr[j].compareTo(arr[selectedIndex]) < 0) {
						selectedIndex = j;
					}
				} else {
					if (arr[j].compareTo(arr[selectedIndex]) > 0) {
						selectedIndex = j;
					}
				}
			}

			if (selectedIndex != i) {
				SortRecorder.markPair(i, selectedIndex);
				swapElements(arr, i, selectedIndex);
				SortRecorder.markSortedPrefix(i);
				SortRecorder.capture(arr, "Moved " + arr[i] + " into position " + i);
			} else {
				SortRecorder.markPlaced(i);
				SortRecorder.markSortedPrefix(i);
				SortRecorder.capture(arr, arr[i] + " is already in position " + i);
			}
		}

		SortRecorder.markSortedPrefix(arr.length - 1);
		SortRecorder.capture(arr, "Sort complete!");
	}

	// Merge Sort (Timmalog)
	public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending) {
		SortRecorder.capture(arr, "Starting array");
		mergeSortRecursive(arr, 0, arr.length - 1, ascending);
		SortRecorder.markSortedPrefix(arr.length - 1);
		SortRecorder.capture(arr, "Sort complete!");
	}

	private static <T extends Comparable<T>> void mergeSortRecursive(T[] arr, int low, int high, boolean ascending) {
		if (low < high) {
			int mid = low + (high - low) / 2;

			SortRecorder.markRegion(low, high);
			SortRecorder.capture(arr, "Splitting range " + low + ".." + high + " at middle " + mid);

			mergeSortRecursive(arr, low, mid, ascending);
			mergeSortRecursive(arr, mid + 1, high, ascending);

			merge(arr, low, mid, high, ascending);

			SortRecorder.markRegion(low, high);
			SortRecorder.capture(arr, "Merged range " + low + ".." + high);
		}
	}

	private static <T extends Comparable<T>> void merge(T[] arr, int low, int mid, int high, boolean ascending) {
		T[] leftHalf = Arrays.copyOfRange(arr, low, mid + 1);
		T[] rightHalf = Arrays.copyOfRange(arr, mid + 1, high + 1);

		int i = 0, j = 0, k = low;

		while (i < leftHalf.length && j < rightHalf.length) {
			comparisonCount++;
			boolean condition = ascending ? leftHalf[i].compareTo(rightHalf[j]) <= 0
					: leftHalf[i].compareTo(rightHalf[j]) >= 0;

			if (condition) {
				arr[k++] = leftHalf[i++];
			} else {
				arr[k++] = rightHalf[j++];
			}
			moveCount++;
		}

		while (i < leftHalf.length) {
			arr[k++] = leftHalf[i++];
			moveCount++;
		}
		while (j < rightHalf.length) {
			arr[k++] = rightHalf[j++];
			moveCount++;
		}
	}

	// Quick Sort (Ambas)
	public static <T extends Comparable<T>> void quickSort(T[] a, boolean ascending) {
		SortRecorder.capture(a, "Starting array");
		quickSortRecursive(a, 0, a.length - 1, ascending);
		SortRecorder.markSortedPrefix(a.length - 1);
		SortRecorder.capture(a, "Sort complete!");
	}

	private static <T extends Comparable<T>> void quickSortRecursive(T[] a, int low, int high, boolean ascending) {
		if (low < high) {
			T pivot = a[high];

			SortRecorder.markRegion(low, high);
			SortRecorder.capture(a, "Partitioning range " + low + ".." + high + " around pivot " + pivot);

			int i = low - 1;
			for (int j = low; j < high; j++) {
				comparisonCount++;
				if (ascending ? a[j].compareTo(pivot) <= 0 : a[j].compareTo(pivot) >= 0) {
					i++;
					swapElements(a, i, j);
				}
			}
			swapElements(a, i + 1, high);

			SortRecorder.markFinal(i + 1);
			SortRecorder.capture(a, "Pivot " + pivot + " locked into final place at index " + (i + 1));

			quickSortRecursive(a, low, i, ascending);
			quickSortRecursive(a, i + 2, high, ascending);
		} else if (low == high) {
			SortRecorder.markFinal(low);
			SortRecorder.capture(a, "Single element locked at index " + low);
		}
	}
}