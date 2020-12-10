package di.unito.it.prog3.libs.email;

public enum Queue {

    RECEIVED, SENT, DRAFTS;

    public String asShortPath() {
        return name().substring(0, 1);
    }

    public static Queue fromShortPath(String shortPath) {
        return switch (shortPath.toUpperCase()) {
            case "R" -> RECEIVED;
            case "S" -> SENT;
            case "D" -> DRAFTS;
            default -> throw new IllegalArgumentException("Non-existing e-mail queue " + shortPath);
        };
    }
}
