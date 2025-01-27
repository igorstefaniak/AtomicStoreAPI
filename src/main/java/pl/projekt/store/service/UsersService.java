package pl.projekt.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pl.projekt.store.model.Users;
import pl.projekt.store.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Users> findUserById(Long userId) {
        return usersRepository.findById(userId);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Optional<Users> findUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public Users createUser(String username, String email, String rawPassword, Users.UserRole role) {
        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return usersRepository.save(user);
    }

    public void updatePassword(Users user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        usersRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        usersRepository.deleteById(userId);
    }

    public boolean verifyPassword(Users user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Users getUserById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public void updateUser(Users user, String username, String email, String rawPassword, Users.UserRole role) {
        if (username != null) user.setUsername(username);
        if (email != null) user.setEmail(email);
        if (role != null) user.setRole(role);
        if (rawPassword != null) user.setPassword(passwordEncoder.encode(rawPassword));
        usersRepository.save(user);
    }

}