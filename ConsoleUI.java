import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private Random random;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
        this.random = new Random();
    }

    public void consoleUI() {
        boolean isRunning = true;

        while (isRunning) {
            ScreenUtil.clearScreen();
            printMainBanner();
            System.out.println("0. Exit");
            System.out.println("1. Sort Integer values");
            System.out.println("2. Sort String values");
            int choice = readNumberBetween("Enter your choice: ", 0, 2);

            switch (choice) {
                case 0:
                    isRunning = false;
                    break;

                case 1:
                    runIntegerFlow();
                    break;

                case 2:
                    runStringFlow();
                    break;
            }
        }

        System.out.println();
        System.out.println("Thank you for using the program. Goodbye!");
    }

    private void runIntegerFlow() {
        boolean inIntegerFlow = true;

        while (inIntegerFlow) {
            ScreenUtil.clearScreen();
            printTitle("Sort Integer Values");
            System.out.println("How do you want to enter the values?");
            System.out.println("0. Back to main menu");
            System.out.println("1. Type the values");
            System.out.println("2. Random values");
            System.out.println("3. Load an activity sample set");
            int choice = readNumberBetween("Enter your choice: ", 0, 3);

            Integer[] values = null;
            switch (choice) {
                case 0:
                    inIntegerFlow = false;
                    break;

                case 1:
                    values = enterIntegerValues();
                    break;

                case 2:
                    values = makeRandomIntegers();
                    break;

                case 3:
                    values = loadSampleSet();
                    break;
            }

            if (values != null) {
                runSortingSession(values, "Integer");
            }
        }
    }

    private Integer[] enterIntegerValues() {
        ScreenUtil.clearScreen();
        printTitle("Type the Integer Values");
        System.out.println("Enter whole numbers separated by spaces.");
        System.out.println("Example: 56 23 45 12");

        while (true) {
            System.out.print("Values: ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Please type at least one value.");
                continue;
            }

            String[] tokens = line.split("\\s+");
            ArrayList<Integer> list = new ArrayList<>();
            boolean hasError = false;

            for (String token : tokens) {
                try {
                    list.add(Integer.parseInt(token));
                } catch (NumberFormatException exception) {
                    System.out.println("\"" + token + "\" is not a whole number. Please try again.");
                    hasError = true;
                    break;
                }
            }

            if (hasError) {
                continue;
            }

            return list.toArray(new Integer[0]);
        }
    }

    private Integer[] makeRandomIntegers() {
        ScreenUtil.clearScreen();
        printTitle("Random Integer Values");
        int count = readNumberBetween("How many values? (1 to 50): ", 1, 50);
        int smallest = readWholeNumber("Smallest value: ");

        int largest;
        while (true) {
            largest = readWholeNumber("Largest value: ");
            if (largest >= smallest) {
                break;
            }
            System.out.println("The largest value must be at least " + smallest + ".");
        }

        Integer[] values = new Integer[count];
        long range = (long) largest - (long) smallest + 1;

        for (int index = 0; index < count; index++) {
            int value = (int) (smallest + (long) (random.nextDouble() * range));
            values[index] = value;
        }

        if (count >= 1) {
            int minIndex = random.nextInt(count);
            values[minIndex] = smallest;

            if (count >= 2) {
                int maxIndex;
                do {
                    maxIndex = random.nextInt(count);
                } while (maxIndex == minIndex);
                values[maxIndex] = largest;
            }
        }

        System.out.println("Generated values: " + Sort.listToString(values));
        waitForEnter();
        return values;
    }

    private Integer[] loadSampleSet() {
        ScreenUtil.clearScreen();
        printTitle("Load an Activity Sample Set");
        System.out.println("0. Back");
        System.out.println("1. 56 23 45 12 5 10 3 89   (Insertion Sort example)");
        System.out.println("2. 24 55 2 48 61 35 57     (Selection Sort example)");
        System.out.println("3. 22 36 74 12 5 90 68 41  (Merge Sort example)");
        System.out.println("4. 102 28 55 9 43 15 84    (Quick Sort example)");
        int choice = readNumberBetween("Enter your choice: ", 0, 4);

        if (choice == 0) {
            return null;
        }
        if (choice == 1) {
            return new Integer[] { 56, 23, 45, 12, 5, 10, 3, 89 };
        }
        if (choice == 2) {
            return new Integer[] { 24, 55, 2, 48, 61, 35, 57 };
        }
        if (choice == 3) {
            return new Integer[] { 22, 36, 74, 12, 5, 90, 68, 41 };
        }
        return new Integer[] { 102, 28, 55, 9, 43, 15, 84 };
    }

    private void runStringFlow() {
        boolean inStringFlow = true;

        while (inStringFlow) {
            ScreenUtil.clearScreen();
            printTitle("Sort String Values");
            System.out.println("How do you want to enter the words?");
            System.out.println("0. Back to main menu");
            System.out.println("1. Type the words");
            System.out.println("2. Random words");
            int choice = readNumberBetween("Enter your choice: ", 0, 2);

            String[] values = null;
            switch (choice) {
                case 0:
                    inStringFlow = false;
                    break;

                case 1:
                    values = enterStringValues();
                    break;

                case 2:
                    values = makeRandomWords();
                    break;
            }

            if (values != null) {
                runSortingSession(values, "String");
            }
        }
    }

    private String[] enterStringValues() {
        ScreenUtil.clearScreen();
        printTitle("Type the String Values");
        System.out.println("Enter words separated by spaces.");
        System.out.println("Example: mango banana apple");

        while (true) {
            System.out.print("Values: ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Please type at least one word.");
                continue;
            }

            return line.split("\\s+");
        }
    }

    private String[] makeRandomWords() {
        ScreenUtil.clearScreen();
        printTitle("Random String Values");
        int count = readNumberBetween("How many words? (1 to 30): ", 1, 30);

        String[] wordBank = {
                "apple", "banana", "cherry", "durian", "eggplant", "fig",
                "grape", "kiwi", "lemon", "mango", "nectarine", "orange",
                "papaya", "quince", "rambutan", "strawberry", "tangerine", "watermelon"
        };

        String[] values = new String[count];
        for (int index = 0; index < count; index++) {
            values[index] = wordBank[random.nextInt(wordBank.length)];
        }

        System.out.println("Generated words: " + Sort.listToString(values));
        waitForEnter();
        return values;
    }

    // Named playback speeds instead of magic numbers scattered in a switch.
    private static final long DELAY_VERY_SLOW_MS = 1800;
    private static final long DELAY_SLOW_MS = 600;
    private static final long DELAY_NORMAL_MS = 250;
    private static final long DELAY_FAST_MS = 60;

    private <T extends Comparable<T>> void runSortingSession(T[] values, String valueType) {
        int algorithmChoice = chooseAlgorithm(values, valueType);
        if (algorithmChoice == 0) {
            return;
        }

        Boolean ascending = chooseOrder(values, valueType, algorithmChoice);
        if (ascending == null) {
            return;
        }
        String orderName = ascending ? "Ascending" : "Descending";

        int playChoice = choosePlayback(values, algorithmChoice, orderName);
        if (playChoice == 0) {
            return;
        }

        boolean manualMode = playChoice == 5;
        boolean textOnly = playChoice == 6;
        long delayMs = delayForPlaybackChoice(playChoice);
        boolean colorsOn = !textOnly && System.console() != null && chooseColors();

        List<SortRecorder.Frame> frames = executeSort(values, algorithmChoice, ascending);
        presentResults(values, frames, valueType, textOnly, colorsOn, delayMs, manualMode);
    }

    private <T extends Comparable<T>> int chooseAlgorithm(T[] values, String valueType) {
        ScreenUtil.clearScreen();
        printTitle("Sorting " + valueType + " Values");
        System.out.println("Your values: " + Sort.listToString(values));
        System.out.println();
        System.out.println("Choose a sorting algorithm:");
        System.out.println("0. Back");
        System.out.println("1. Insertion Sort");
        System.out.println("2. Selection Sort");
        System.out.println("3. Merge Sort");
        System.out.println("4. Quick Sort");
        return readNumberBetween("Enter your choice: ", 0, 4);
    }

    // Returns null if the user chose to go back.
    private <T extends Comparable<T>> Boolean chooseOrder(T[] values, String valueType, int algorithmChoice) {
        ScreenUtil.clearScreen();
        printTitle("Choose the Sorting Order");
        System.out.println("0. Back");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        int orderChoice = readNumberBetween("Enter your choice: ", 0, 2);
        if (orderChoice == 0) {
            return null;
        }
        return orderChoice == 1;
    }

    private <T extends Comparable<T>> int choosePlayback(T[] values, int algorithmChoice, String orderName) {
        ScreenUtil.clearScreen();
        printTitle("Playback Options");
        System.out.println("Running " + algorithmName(algorithmChoice) + " (" + orderName + ") on:");
        System.out.println(Sort.listToString(values));
        System.out.println();
        System.out.println("0. Back");
        System.out.println("1. Very slow animation");
        System.out.println("2. Slow animation");
        System.out.println("3. Normal animation");
        System.out.println("4. Fast animation");
        System.out.println("5. Manual (Enter per step)");
        System.out.println("6. Text report only");
        return readNumberBetween("Enter your choice: ", 0, 6);
    }

    private long delayForPlaybackChoice(int playChoice) {
        switch (playChoice) {
            case 1:
                return DELAY_VERY_SLOW_MS;
            case 2:
                return DELAY_SLOW_MS;
            case 3:
                return DELAY_NORMAL_MS;
            case 4:
                return DELAY_FAST_MS;
            default:
                return 0;
        }
    }

    private boolean chooseColors() {
        ScreenUtil.clearScreen();
        printTitle("Colors");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int colorChoice = readNumberBetween("Enter your choice: ", 1, 2);
        return colorChoice == 1;
    }

    private <T extends Comparable<T>> List<SortRecorder.Frame> executeSort(T[] values, int algorithmChoice,
            boolean ascending) {
        Sort.resetCounters();
        SortRecorder.clear();

        switch (algorithmChoice) {
            case 1:
                Sort.insertionSort(values, ascending);
                break;

            case 2:
                Sort.selectionSort(values, ascending);
                break;

            case 3:
                Sort.mergeSort(values, ascending);
                break;

            case 4:
                Sort.quickSort(values, ascending);
                break;
        }

        return SortRecorder.getFrames();
    }

    private <T> void presentResults(T[] values, List<SortRecorder.Frame> frames, String valueType,
            boolean textOnly, boolean colorsOn, long delayMs, boolean manualMode) {
        if (!textOnly) {
            SortAnimator.replay(frames, valueType, colorsOn, delayMs, manualMode, scanner);
        }

        SortAnimator.reportSteps(frames);

        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Final sorted list: " + Sort.listToString(values));
        System.out.println("Comparisons made: " + Sort.getComparisonCount());
        System.out.println("Element moves: " + Sort.getMoveCount());
        waitForEnter();
    }

    private String algorithmName(int algorithmChoice) {
        if (algorithmChoice == 1) {
            return "Insertion Sort";
        }
        if (algorithmChoice == 2) {
            return "Selection Sort";
        }
        if (algorithmChoice == 3) {
            return "Merge Sort";
        }
        return "Quick Sort";
    }

    private int readNumberBetween(String prompt, int smallest, int largest) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);
                if (value >= smallest && value <= largest) {
                    return value;
                }
                System.out.println("Enter a number from " + smallest + " to " + largest + ".");
            } catch (NumberFormatException exception) {
                System.out.println("That is not a number. Please try again.");
            }
        }
    }

    private int readWholeNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException exception) {
                System.out.println("That is not a whole number. Please try again.");
            }
        }
    }

    private void waitForEnter() {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void printTitle(String title) {
        System.out.println(title);
        System.out.println("--------------------------------");
        System.out.println();
    }

    private void printMainBanner() {
        System.out.println("+-------------------------------------+");
        System.out.println("|   Welcome to our Sorting Program    |");
        System.out.println("+-------------------------------------+");
        System.out.println();
    }

}