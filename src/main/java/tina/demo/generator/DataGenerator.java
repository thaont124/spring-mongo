package tina.demo.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tina.demo.entity.*;
import tina.demo.repository.elasticsearch.CategoryRepository;
import tina.demo.repository.jpa.OrderDetailRepository;
import tina.demo.repository.jpa.OrderRepository;
import tina.demo.repository.jpa.UserRepository;
import tina.demo.repository.mongo.ProductRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataGenerator implements CommandLineRunner {
    private static final String[] NAMES = {"Electronics", "Books", "Clothing", "Home", "Sports", "Toys", "Groceries", "Beauty", "Automotive", "Jewelry"};
    private static final String[] DESCRIPTIONS = {"High quality", "Latest model", "On sale", "Popular item", "Top-rated", "Exclusive", "Limited edition", "Best seller", "Customer favorite", "Eco-friendly"};


    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if the databases are empty
        if (userRepository.count() == 0 && productRepository.count() == 0 && orderRepository.count() == 0 && orderDetailRepository.count() == 0) {
            // Create 30 Users
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 30; i++) {
                users.add(new User("User " + i, "user" + i + "@example.com", "password" + i));
            }
            userRepository.saveAll(users);

            // Create 30 Products
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 1000000; i++) {
                products.add(new Product(UUID.randomUUID().toString(), "Product " + i, "Description for product " + i, 10.0 + i, "Category " + i, "SELLING", null));
            }
            productRepository.saveAll(products);

            List<Category> categories = new ArrayList<>();
            Random random = new Random();

            for (int i = 1; i <= 1_000_000; i++) {
                String randomName = NAMES[random.nextInt(NAMES.length)] + " " + (i % 100);
                String randomDescription = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)] + " " + i;

                categories.add(new Category(UUID.randomUUID().toString(), randomName, randomDescription));
            }


            categoryRepository.saveAll(categories);

            // Create 100 Orders and OrderDetails
            List<Order> orders = new ArrayList<>();
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                User user = users.get(i % users.size());
                Order order = new Order(user, LocalDate.now(), 0.0);
                orders.add(order);

                int detailsCount = (int) (Math.random() * 5) + 1; // 1 to 5 products per order
                double totalAmount = 0.0;

                for (int j = 0; j < detailsCount; j++) {
                    Product product = products.get(j % products.size());
                    int quantity = (int) (Math.random() * 10) + 1; // 1 to 10 quantity
                    double price = product.getPrice() * quantity;
                    totalAmount += price;

                    orderDetails.add(new OrderDetail(UUID.randomUUID().toString(), order, product.getId(), quantity, price));
                }

                order.setTotalAmount(totalAmount);
            }

            orderRepository.saveAll(orders);
            orderDetailRepository.saveAll(orderDetails);
        }
    }
}
