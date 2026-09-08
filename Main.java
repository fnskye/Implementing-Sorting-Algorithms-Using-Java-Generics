import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUI consoleUI = new ConsoleUI(scanner);
        consoleUI.consoleUI();
        scanner.close();
    }
}
