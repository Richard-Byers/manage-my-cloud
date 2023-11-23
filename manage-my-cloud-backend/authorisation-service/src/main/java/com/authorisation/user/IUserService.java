package com.authorisation.user;

import com.authorisation.registration.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> getUsers();

    User registerUser(RegistrationRequest registrationRequest);

    Optional<User> findUserByEmail(String email);
}
