package pl.projekt.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.store.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}
