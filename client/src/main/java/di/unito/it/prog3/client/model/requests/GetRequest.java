package di.unito.it.prog3.client.model.requests;

import di.unito.it.prog3.libs.email.Email.ID;

public class GetRequest {

    public static Request exactly(ID id) {
        return () -> {

        };
    }

    public static Request offsetMany(ID offset, int many) {
        return () -> {

        };
    }

}
