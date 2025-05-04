package hr.algebra.webshop.service;

import hr.algebra.webshop.exception.EntityNotFoundException;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: " + username + " not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
