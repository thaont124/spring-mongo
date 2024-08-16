package tina.demo.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tina.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
