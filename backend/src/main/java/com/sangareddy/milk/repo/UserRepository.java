package com.sangareddy.milk.repo;

import com.sangareddy.milk.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByNameIgnoreCase(String name);
}
