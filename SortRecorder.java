import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortRecorder {

    public static class Frame {
        public final Object[] values;
        public final String caption;
        public final int pairFirst;
        public final int pairSecond;
        public final int regionFrom;
        public final int regionTo;
        public final int spotlight;
        public final int sortedPrefixTo;
        public final ArrayList<Integer> finalIndices;

        public Frame(Object[] snapshot, String caption, int pairFirst, int pairSecond,
                     int regionFrom, int regionTo, int spotlight, int sortedPrefixTo,
                     ArrayList<Integer> finalIndices) {
            this.values = snapshot;
            this.caption = caption;
            this.pairFirst = pairFirst;
            this.pairSecond = pairSecond;
            this.regionFrom = regionFrom;
            this.regionTo = regionTo;
            this.spotlight = spotlight;
            this.sortedPrefixTo = sortedPrefixTo;
            this.finalIndices = new ArrayList<>(finalIndices);
        }
    }

    private static ArrayList<Frame> frames = new ArrayList<>();

    private static int pendingPairFirst = -1;
    private static int pendingPairSecond = -1;
    private static int pendingRegionFrom = -1;
    private static int pendingRegionTo = -1;
    private static int pendingSpotlight = -1;
    private static int sortedPrefixTo = -1;
    private static ArrayList<Integer> finalIndices = new ArrayList<>();

    public static void clear() {
        frames.clear();
        pendingPairFirst = -1;
        pendingPairSecond = -1;
        pendingRegionFrom = -1;
        pendingRegionTo = -1;
        pendingSpotlight = -1;
        sortedPrefixTo = -1;
        finalIndices.clear();
    }

    public static List<Frame> getFrames() {
        return frames;
    }

    // Highlights two elements that are being swapped or compared.
    public static void markPair(int firstIndex, int secondIndex) {
        pendingPairFirst = firstIndex;
        pendingPairSecond = secondIndex;
    }

    // Highlights a whole range that is actively being worked on.
    public static void markRegion(int fromIndex, int toIndex) {
        pendingRegionFrom = fromIndex;
        pendingRegionTo = toIndex;
    }

    // Spotlights a single element for one frame.
    public static void markPlaced(int index) {
        pendingSpotlight = index;
    }

    // Marks one element as locked into its final spot (stays for the rest of the run).
    public static void markFinal(int index) {
        if (!finalIndices.contains(index)) {
            finalIndices.add(index);
        }
    }

    // Marks the growing sorted prefix (stays for the rest of the run).
    public static void markSortedPrefix(int upToIndex) {
        if (upToIndex > sortedPrefixTo) {
            sortedPrefixTo = upToIndex;
        }
    }

    // Takes a snapshot of the array together with the pending marks, then
    // clears the transient marks so the next capture starts fresh.
    public static <T> void capture(T[] values, String caption) {
        Object[] snapshot = Arrays.copyOf(values, values.length);

        Frame frame = new Frame(snapshot, caption,
                pendingPairFirst, pendingPairSecond,
                pendingRegionFrom, pendingRegionTo,
                pendingSpotlight, sortedPrefixTo, finalIndices);

        frames.add(frame);

        pendingPairFirst = -1;
        pendingPairSecond = -1;
        pendingRegionFrom = -1;
        pendingRegionTo = -1;
        pendingSpotlight = -1;
    }
}
