package tina.demo.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tina.demo.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    @Query(value="{ 'name' : { $regex: ?0, $options: 'i' } }")
    List<Product> findByKeyword(@Param("keyword") String keyword);

//    @Query(value="{ 'name' : ?0 }")
//    List<Product> findByKeyword(String keyword);
}