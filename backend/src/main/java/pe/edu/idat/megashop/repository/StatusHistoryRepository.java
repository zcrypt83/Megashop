package pe.edu.idat.megashop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.idat.megashop.model.StatusHistory;

public interface StatusHistoryRepository extends MongoRepository<StatusHistory, String> {
}
