public class ScreenUtil {
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                clearScreenAnsi();
            }
        } catch (Exception e) {
            System.out.println("\n\n\n");
        }
    }

    public static void clearScreenAnsi() {
        System.out.print("\u001B[2J\u001B[H");
        System.out.flush();
    }
}