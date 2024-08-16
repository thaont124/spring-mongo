package tina.demo.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import tina.demo.entity.Category;

import java.util.List;

public interface CategoryRepository extends ElasticsearchRepository<Category, String> {
    List<Category> findByNameContainingOrDescriptionContaining(String name, String description);
}