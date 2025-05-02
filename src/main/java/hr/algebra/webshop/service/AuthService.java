package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.LoginDto;
import hr.algebra.webshop.dto.RegisterDto;
import hr.algebra.webshop.dto.UserDto;
import hr.algebra.webshop.exception.EntityAlreadyExistsException;
import hr.algebra.webshop.exception.EntityNotFoundException;
import hr.algebra.webshop.exception.InvalidPasswordException;
import hr.algebra.webshop.model.Role;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto register(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new EntityAlreadyExistsException("Username is already in use");
        }

        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new EntityAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRole(registerDto.getRole() == null ? Role.USER : registerDto.getRole());

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    public UserDto login(LoginDto loginDto) {
        Optional<User> optionalUser = userRepository.findByUsername(loginDto.getUsername());
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Username doesnt exist");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Wrong password");
        }
        return toDto(user);
    }

    public UserDto getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    private UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
