package com.sangareddy.milk.repo;

import com.sangareddy.milk.model.Subscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	Optional<Subscription> findByUserId(Long userId);
	void deleteByUserId(Long userId);
}
