package di.unito.it.prog3.libs.email;

public enum Queue {

    RECEIVED, SENT;

    public String asShortPath() {
        return name().substring(0, 1);
    }

    public static Queue fromShortPath(String shortPath) {
        return switch (shortPath.toUpperCase()) {
            case "R" -> RECEIVED;
            case "S" -> SENT;
            default -> throw new IllegalArgumentException("Unknown e-mail queue " + shortPath);
        };
    }
}
