package tina.demo.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tina.demo.entity.Order;
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
