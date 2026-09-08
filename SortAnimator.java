import java.util.List;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

public class SortAnimator {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private static final int VIEW_WIDTH = 100;

    private static boolean jansiInstalled = false;

    public static void replay(List<SortRecorder.Frame> frames, String valueType,
            boolean colorsOn, long delayMs, boolean manualMode, Scanner scanner) {
        boolean interactive = System.console() != null;
        boolean useColors = colorsOn && interactive;

        if (useColors && !jansiInstalled) {
            AnsiConsole.systemInstall();
            jansiInstalled = true;
        }

        int totalFrames = frames.size();

        for (int index = 0; index < totalFrames; index++) {
            SortRecorder.Frame frame = frames.get(index);
            int stepNumber = index + 1;

            if (interactive) {
                clearScreen(useColors);
            } else {
                System.out.println();
                System.out.println("------- frame " + stepNumber + " of " + totalFrames + " -------");
            }

            System.out.println("Step " + stepNumber + " of " + totalFrames + " - " + frame.caption);

            if (valueType.equals("Integer")) {
                drawIntegerFrame(frame, useColors);
            } else {
                drawStringFrame(frame, useColors);
            }

            // Manual pausing and timed delays work no matter what kind of
            // console this is running in - reading a line and sleeping a
            // thread don't need a real terminal. Only screen-clearing style
            // and color output depend on `interactive` above.
            if (manualMode && stepNumber < totalFrames) {
                System.out.println();
                System.out.print("Press Enter for the next step...");
                scanner.nextLine();
            } else if (!manualMode && stepNumber < totalFrames && delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException exception) {
                    // ignore and continue
                }
            }
        }
    }

    public static void reportSteps(List<SortRecorder.Frame> frames) {
        System.out.println();
        System.out.println("Step report:");
        int stepNumber = 1;

        for (SortRecorder.Frame frame : frames) {
            System.out.println(stepNumber + ") " + frame.caption);
            System.out.println("   values: " + valuesToString(frame.values));
            stepNumber++;
        }
    }

    private static String valuesToString(Object[] values) {
        String result = "";
        for (int index = 0; index < values.length; index++) {
            result += values[index];
            if (index < values.length - 1) {
                result += " ";
            }
        }
        return result;
    }

    private static void clearScreen(boolean useColors) {
        if (useColors) {
            ScreenUtil.clearScreenAnsi();
        } else {
            ScreenUtil.clearScreen();
        }
    }

    private static boolean isPairColumn(SortRecorder.Frame frame, int columnIndex) {
        return columnIndex == frame.pairFirst || columnIndex == frame.pairSecond;
    }

    private static boolean isSpotlightColumn(SortRecorder.Frame frame, int columnIndex) {
        return columnIndex == frame.spotlight;
    }

    private static boolean isRegionColumn(SortRecorder.Frame frame, int columnIndex) {
        return frame.regionFrom >= 0 && columnIndex >= frame.regionFrom && columnIndex <= frame.regionTo;
    }

    private static boolean isSortedColumn(SortRecorder.Frame frame, int columnIndex) {
        if (frame.sortedPrefixTo >= 0 && columnIndex <= frame.sortedPrefixTo) {
            return true;
        }
        return frame.finalIndices.contains(columnIndex);
    }

    private static String colorForColumn(SortRecorder.Frame frame, int columnIndex, boolean useColors) {
        if (!useColors) {
            return "";
        }
        if (isPairColumn(frame, columnIndex) || isSpotlightColumn(frame, columnIndex)) {
            return ANSI_YELLOW;
        }
        if (isRegionColumn(frame, columnIndex)) {
            return ANSI_YELLOW;
        }
        if (isSortedColumn(frame, columnIndex)) {
            return ANSI_GREEN;
        }
        return "";
    }

    // When the list is too wide to fit readable columns, show a window of
    // columns centered on the element(s) the algorithm is currently working on.
    private static int[] pickWindow(SortRecorder.Frame frame, int count, int columnWidth) {
        int windowSize = VIEW_WIDTH / columnWidth;
        if (windowSize < 4) {
            windowSize = 4;
        }
        if (windowSize > count) {
            windowSize = count;
        }
        if (windowSize >= count) {
            return new int[] { 0, count - 1 };
        }

        int center = -1;
        if (frame.pairFirst >= 0) {
            center = (frame.pairFirst + frame.pairSecond) / 2;
        } else if (frame.spotlight >= 0) {
            center = frame.spotlight;
        } else if (frame.regionFrom >= 0) {
            center = (frame.regionFrom + frame.regionTo) / 2;
        }

        int start;
        if (center < 0) {
            start = 0;
        } else {
            start = center - windowSize / 2;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > count - windowSize) {
            start = count - windowSize;
        }

        return new int[] { start, start + windowSize - 1 };
    }

    private static void drawIntegerFrame(SortRecorder.Frame frame, boolean useColors) {
        Object[] values = frame.values;
        int count = values.length;

        long[] rawValues = new long[count];
        long smallest = Long.MAX_VALUE;
        long largest = Long.MIN_VALUE;

        for (int index = 0; index < count; index++) {
            long number = ((Number) values[index]).longValue();
            rawValues[index] = number;
            if (number < smallest) {
                smallest = number;
            }
            if (number > largest) {
                largest = number;
            }
        }

        long range = largest - smallest + 1;
        int maxRows = 12;
        int[] heights = new int[count];
        int maximumHeight = 0;

        for (int index = 0; index < count; index++) {
            long scaled = rawValues[index] - smallest + 1;
            int height = (int) Math.round(scaled * (double) maxRows / range);
            if (height < 1) {
                height = 1;
            }
            heights[index] = height;
            if (height > maximumHeight) {
                maximumHeight = height;
            }
        }

        int labelWidth = 1;
        for (int index = 0; index < count; index++) {
            int length = String.valueOf(rawValues[index]).length();
            if (length > labelWidth) {
                labelWidth = length;
            }
        }

        // Increase padding to 3 extra spaces of width
        int columnWidth = labelWidth + 3;
        if (columnWidth < 4) {
            columnWidth = 4;
        }

        int[] window = pickWindow(frame, count, columnWidth);
        int fromColumn = window[0];
        int toColumn = window[1];

        System.out.println();
        if (toColumn - fromColumn + 1 < count) {
            System.out.println("Viewing columns " + fromColumn + " to " + toColumn + " of " + count);
        }

        // Pass useColors to the updated markerRow method
        System.out.println(markerRow(frame, fromColumn, toColumn, columnWidth, useColors));
        System.out.println(valueRow(rawValues, fromColumn, toColumn, columnWidth, frame, useColors));
        System.out.println();

        for (int row = maximumHeight; row >= 1; row--) {
            StringBuilder line = new StringBuilder();
            for (int column = fromColumn; column <= toColumn; column++) {
                boolean filled = heights[column] >= row;
                String chunk = barColumn(filled, columnWidth);
                if (filled) {
                    String color = colorForColumn(frame, column, useColors);
                    chunk = paint(chunk, color);
                }
                line.append(chunk);
            }
            System.out.println(line.toString());
        }

        System.out.println(baselineRow(fromColumn, toColumn, columnWidth));
        System.out.println(indexRow(fromColumn, toColumn, columnWidth));

        if (toColumn - fromColumn + 1 < count) {
            System.out.println("Overview: " + overviewStrip(frame, count, fromColumn, toColumn));
        }

        printFooters(frame, useColors);
    }

    private static void drawStringFrame(SortRecorder.Frame frame, boolean useColors) {
        Object[] values = frame.values;
        int count = values.length;

        int cellWidth = 3;
        for (int index = 0; index < count; index++) {
            int length = String.valueOf(values[index]).length();
            if (length + 2 > cellWidth) {
                cellWidth = length + 2;
            }
        }

        int totalColumnWidth = cellWidth + 3;

        int[] window = pickWindow(frame, count, totalColumnWidth);
        int fromColumn = window[0];
        int toColumn = window[1];

        System.out.println();
        if (toColumn - fromColumn + 1 < count) {
            System.out.println("Viewing cells " + fromColumn + " to " + toColumn + " of " + count);
        }

        StringBuilder line = new StringBuilder();
        for (int column = fromColumn; column <= toColumn; column++) {
            String word = String.valueOf(values[column]);
            String cell = padCentered(word, cellWidth);
            String color = colorForColumn(frame, column, useColors);

            String block = "|" + cell + "|";
            line.append(paint(block, color));

            line.append(" ");
        }
        System.out.println(line.toString());

        // Pass useColors to the updated markerRow method
        System.out.println(markerRow(frame, fromColumn, toColumn, totalColumnWidth, useColors));

        if (toColumn - fromColumn + 1 < count) {
            System.out.println("Overview: " + overviewStrip(frame, count, fromColumn, toColumn));
        }

        printFooters(frame, useColors);
    }

    private static String overviewStrip(SortRecorder.Frame frame, int count, int fromColumn, int toColumn) {
        StringBuilder strip = new StringBuilder();
        for (int column = 0; column < count; column++) {
            char mark = ' ';
            if (column >= fromColumn && column <= toColumn) {
                mark = '.';
            }
            if (column == frame.pairFirst) {
                mark = '^';
            } else if (column == frame.pairSecond) {
                mark = 'v';
            } else if (column == frame.spotlight) {
                mark = '*';
            } else if (column == frame.regionFrom) {
                mark = '[';
            } else if (column == frame.regionTo) {
                mark = ']';
            } else if (mark == ' ' && isSortedColumn(frame, column)) {
                mark = '=';
            }
            strip.append(mark);
        }
        return strip.toString();
    }

    private static String paint(String text, String color) {
        if (color.isEmpty()) {
            return text;
        }
        return color + text + ANSI_RESET;
    }

    private static String markerRow(SortRecorder.Frame frame, int fromColumn, int toColumn, int columnWidth,
            boolean useColors) {
        StringBuilder row = new StringBuilder();
        for (int column = fromColumn; column <= toColumn; column++) {
            String marker = " ";
            if (isPairColumn(frame, column)) {
                if (column == frame.pairFirst) {
                    marker = "^";
                } else {
                    marker = "v";
                }
            } else if (isSpotlightColumn(frame, column)) {
                marker = "*";
            } else if (column == frame.regionFrom) {
                marker = "[";
            } else if (column == frame.regionTo) {
                marker = "]";
            }

            // Apply the color to the marker before appending
            String color = colorForColumn(frame, column, useColors);
            row.append(paint(padCentered(marker, columnWidth), color));
        }
        return row.toString();
    }

    private static String valueRow(long[] rawValues, int fromColumn, int toColumn, int columnWidth,
            SortRecorder.Frame frame, boolean useColors) {
        StringBuilder row = new StringBuilder();
        for (int column = fromColumn; column <= toColumn; column++) {
            String label = String.valueOf(rawValues[column]);
            String color = colorForColumn(frame, column, useColors);
            row.append(paint(padCentered(label, columnWidth), color));
        }
        return row.toString();
    }

    private static String indexRow(int fromColumn, int toColumn, int columnWidth) {
        StringBuilder row = new StringBuilder();
        for (int column = fromColumn; column <= toColumn; column++) {
            row.append(padCentered(String.valueOf(column), columnWidth));
        }
        return row.toString();
    }

    private static void printFooters(SortRecorder.Frame frame, boolean useColors) {
        if (frame.sortedPrefixTo >= 0) {
            String text = "Sorted so far: positions 0 to " + frame.sortedPrefixTo;
            System.out.println();
            if (useColors) {
                System.out.println(paint("[ " + text + " ]", ANSI_GREEN));
            } else {
                System.out.println("[ " + text + " ]");
            }
        }
        if (!frame.finalIndices.isEmpty()) {
            String text = "Final spots locked: positions " + valuesToString(frame.finalIndices.toArray());
            System.out.println();
            if (useColors) {
                System.out.println(paint("[ " + text + " ]", ANSI_GREEN));
            } else {
                System.out.println("[ " + text + " ]");
            }
        }
    }

    private static String padCentered(String content, int width) {
        int missing = width - content.length();
        if (missing <= 0) {
            return content;
        }
        int leftPad = missing / 2;
        int rightPad = missing - leftPad;
        String result = content;
        for (int index = 0; index < leftPad; index++) {
            result = " " + result;
        }
        for (int index = 0; index < rightPad; index++) {
            result += " ";
        }
        return result;
    }

    private static String barColumn(boolean filled, int columnWidth) {
        StringBuilder result = new StringBuilder();
        if (filled) {
            // Print solid blocks leaving a 1-character gap between columns
            for (int i = 0; i < columnWidth - 1; i++) {
                result.append("\u2588");
            }
            result.append(" ");
        } else {
            // Fill empty space if the column is not filled at this height
            for (int i = 0; i < columnWidth; i++) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private static String baselineRow(int fromColumn, int toColumn, int columnWidth) {
        StringBuilder row = new StringBuilder();
        for (int column = fromColumn; column <= toColumn; column++) {
            for (int i = 0; i < columnWidth; i++) {
                row.append("-");
            }
        }
        return row.toString();
    }
}