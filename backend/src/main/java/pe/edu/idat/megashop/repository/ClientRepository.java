package pe.edu.idat.megashop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.idat.megashop.model.Client;

public interface ClientRepository extends MongoRepository<Client, String> {
    boolean existsByEmail(String email);
}
