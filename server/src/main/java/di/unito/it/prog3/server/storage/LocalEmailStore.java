package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;

import java.util.List;

public abstract class LocalEmailStore implements EmailStore<Void, Boolean, Email, List<Email>> {

}
