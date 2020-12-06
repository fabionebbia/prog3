package di.unito.it.prog3.client.model;

public class EmailStatus extends StatusBase {

    public static final EmailStatus CORRECT     = new EmailStatus(OK,  "");
    public static final EmailStatus BLANK       = new EmailStatus(ERR, "E-mail is required");
    public static final EmailStatus MALFORMED   = new EmailStatus(ERR, "E-mail is malformed");
    public static final EmailStatus UNKNOWN     = new EmailStatus(ERR, "Unknown e-mail address");

    protected EmailStatus(boolean isError, String message) {
        super(isError, message);
    }

    /*
    CORRECT    (OK,  ""),
    BLANK      (ERR, "E-mail cannot be blank"),
    MALFORMED  (ERR, "E-mail is malformed"),
    UNKNOWN    (ERR, "Unknown e-mail address");
    */


}
