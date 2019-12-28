package io.github.hobbstech.bpm.commons;

public interface UserRepository {

    AbstractUser findByUsername(String authenticatedUser);

}
