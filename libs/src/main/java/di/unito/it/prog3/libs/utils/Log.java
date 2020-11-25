package di.unito.it.prog3.libs.utils;

public class Log {

    private static final String INFO  = " [INFO]: ";
    private static final String ERROR = "[ERROR]: ";
    private static final String WARN  = " [WARN]: ";

    public static void info(String message) {
        print(INFO, message);
    }

    public static void error(String message) {
        print(ERROR, message);
    }

    public static void warn(String message) {
        print(WARN, message);
    }

    private static void print(String label, String message) {
        System.out.println(label + message);
        System.out.flush();
    }
}
