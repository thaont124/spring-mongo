package tina.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tina.demo.entity.Product;
import tina.demo.repository.mongo.ProductRepository;
import tina.demo.utils.UpdateUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String HASH_KEY = "product";
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;  // Cập nhật kiểu của RedisTemplate
    private final HashOperations<String, String, Product> hashOperations;  // Cập nhật kiểu của HashOperations
    private final ObjectMapper objectMapper = new ObjectMapper();
    public List<Product> findByKeyword(String key) {
        return productRepository.findByKeyword(key);
    }

    public List<Product> getAllProducts(int page, int size) {
        String cacheKey = HASH_KEY + "::page=" + page + "::size=" + size;

        // Kiểm tra cache
        String cachedProductsJson = (String) redisTemplate.opsForValue().get(cacheKey);
        List<Product> cachedProducts = null;
        if (cachedProductsJson != null) {
            try {
                cachedProducts = objectMapper.readValue(cachedProductsJson, new TypeReference<List<Product>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cachedProducts != null) {
            return cachedProducts;
        }

        // Nếu không có trong cache, lấy từ DB và lưu vào cache
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();

        // Lưu vào cache
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(products), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public Product getProductById(String id) {
        Product cachedProduct = hashOperations.get(HASH_KEY, id);
        if (cachedProduct != null) {
            return cachedProduct;
        }

        Product product = productRepository.findById(id).orElseThrow();
        hashOperations.put(HASH_KEY, id, product);
        return product;
    }

    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        hashOperations.put(HASH_KEY, savedProduct.getId(), savedProduct);
        return savedProduct;
    }

    public void deleteProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("INACTIVE");
        productRepository.save(product);
        hashOperations.delete(HASH_KEY, id);
    }

    public Product updateProduct(String id, Product updatedProduct) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        UpdateUtils.updateEntityFromDTO(product, updatedProduct);

        product = productRepository.save(product);
        hashOperations.put(HASH_KEY, product.getId(), product);
        return product;
    }
}
