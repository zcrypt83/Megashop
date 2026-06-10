package pe.edu.idat.megashop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.idat.megashop.model.Product;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findTop100ByOrderByNombreAsc();
    List<Product> findTop100ByCategoriaIdOrderByNombreAsc(String categoriaId);
    List<Product> findTop100ByNombreContainingIgnoreCaseOrderByNombreAsc(String query);
    List<Product> findTop100ByCategoriaIdAndNombreContainingIgnoreCaseOrderByNombreAsc(String categoriaId, String query);
}
