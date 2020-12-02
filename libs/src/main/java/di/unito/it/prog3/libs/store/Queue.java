package di.unito.it.prog3.libs.store;

public enum Queue {

    RECEIVED, SENT, DRAFTS;

    public String asShortPath() {
        return name().substring(0, 1);
    }

    public static Queue fromShortPath(String shortPath) {
        switch (shortPath.toUpperCase()) {
            case "R": return RECEIVED;
            case "S": return SENT;
            case "D": return DRAFTS;
            default: throw new IllegalArgumentException("Non-existing e-mail queue " + shortPath);
        }
    }
}
