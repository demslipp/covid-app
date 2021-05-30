package ru.covid.app.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.stereotype.Component;

import static ru.covid.app.exception.InvocationError.FIREBASE_INVOCATION_ERROR;

@Component
public class AuthService {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public String getUuidFromToken(String token) {
        try {
            return firebaseAuth.verifyIdToken(token).getUid();
        } catch (Exception e) {
            throw FIREBASE_INVOCATION_ERROR.exception(e);
        }
    }
}
