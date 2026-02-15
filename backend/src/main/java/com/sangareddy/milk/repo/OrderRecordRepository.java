package com.sangareddy.milk.repo;

import com.sangareddy.milk.model.OrderRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRecordRepository extends JpaRepository<OrderRecord, Long> {
	List<OrderRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
	List<OrderRecord> findTop50ByOrderByCreatedAtDesc();
}
