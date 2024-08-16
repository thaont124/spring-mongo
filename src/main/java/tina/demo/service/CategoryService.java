package tina.demo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tina.demo.entity.Category;
import tina.demo.entity.Product;
import tina.demo.repository.elasticsearch.CategoryRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ElasticsearchClient elasticsearchClient;

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        return categoriesPage.getContent();
    }

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public Category updateCategory(String id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> searchCategoriesByNameOrDescription(String searchTerm) throws IOException {
        Query query = QueryBuilders.bool(b -> b
                .should(s -> s.match(m -> m.field("name").query(searchTerm)))
                .should(s -> s.match(m -> m.field("description").query(searchTerm)))
        );

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("category")
                .query(query)
                .build();

        SearchResponse<Category> searchResponse = elasticsearchClient.search(searchRequest, Category.class);

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}
