package com.authorisation.user;

import com.authorisation.registration.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest registrationRequest) {

        Optional<User> userOptional = this.findUserByEmail(registrationRequest.email());

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", registrationRequest.email()));
        }

        var newUser = new User();

        newUser.setFirstName(registrationRequest.firstName());
        newUser.setLastName(registrationRequest.lastName());
        newUser.setEmail(registrationRequest.email());
        newUser.setPassword();
        newUser.setRole(registrationRequest.role());

        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
