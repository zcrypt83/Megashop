package pe.edu.idat.megashop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.idat.megashop.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
