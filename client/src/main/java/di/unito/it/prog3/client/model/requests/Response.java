package di.unito.it.prog3.client.model.requests;

import di.unito.it.prog3.libs.model.Error;

public interface Response {

    Error toError();

}
