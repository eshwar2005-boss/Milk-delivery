package com.sangareddy.milk.api;

import com.sangareddy.milk.api.dto.OrderResponse;
import com.sangareddy.milk.api.dto.UpdateUserRequest;
import com.sangareddy.milk.api.dto.UserResponse;
import com.sangareddy.milk.model.OrderRecord;
import com.sangareddy.milk.model.Subscription;
import com.sangareddy.milk.model.User;
import com.sangareddy.milk.repo.OrderRecordRepository;
import com.sangareddy.milk.repo.SubscriptionRepository;
import com.sangareddy.milk.repo.UserRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private final UserRepository userRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final OrderRecordRepository orderRecordRepository;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);

	public AdminController(UserRepository userRepository, SubscriptionRepository subscriptionRepository,
			OrderRecordRepository orderRecordRepository) {
		this.userRepository = userRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.orderRecordRepository = orderRecordRepository;
	}

	@GetMapping("/users")
	public List<UserResponse> users() {
		return userRepository.findAll().stream()
				.map(user -> toUserResponse(user, subscriptionRepository.findByUserId(user.getId()).orElse(null)))
				.toList();
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserResponse> user(@PathVariable Long id) {
		return userRepository.findById(id)
				.map(user -> ResponseEntity.ok(toUserResponse(user,
						subscriptionRepository.findByUserId(user.getId()).orElse(null))))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
			@RequestBody UpdateUserRequest request) {
		return userRepository.findById(id).map(user -> {
			if (request.getName() != null && !request.getName().isBlank()) {
				user.setName(request.getName().trim());
			}
			if (request.getDeliveryArea() != null) {
				user.setDeliveryArea(request.getDeliveryArea());
			}
			if (request.getDeliveryFee() != null) {
				user.setDeliveryFee(request.getDeliveryFee());
			}
			if (request.getWalletBalance() != null) {
				user.setWalletBalance(request.getWalletBalance());
			}
			userRepository.save(user);
			Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
			return ResponseEntity.ok(toUserResponse(user, subscription));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		orderRecordRepository.findByUserIdOrderByCreatedAtDesc(id).forEach(orderRecordRepository::delete);
		subscriptionRepository.deleteByUserId(id);
		userRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/orders")
	public List<OrderResponse> orders() {
		return orderRecordRepository.findTop50ByOrderByCreatedAtDesc().stream()
				.map(this::toOrderResponse)
				.toList();
	}

	@DeleteMapping("/orders/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		if (!orderRecordRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		orderRecordRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private UserResponse toUserResponse(User user, Subscription subscription) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setName(user.getName());
		response.setWalletBalance(user.getWalletBalance());
		response.setDeliveryFee(user.getDeliveryFee());
		response.setDeliveryArea(user.getDeliveryArea());
		if (subscription != null) {
			response.setSubscription(toSubscriptionResponse(subscription));
		}
		return response;
	}

	private OrderResponse toOrderResponse(OrderRecord record) {
		OrderResponse response = new OrderResponse();
		response.setId(record.getId());
		response.setUserName(record.getUser().getName());
		response.setDate(dateFormatter.format(record.getOrderDate()));
		response.setItem(record.getItem());
		response.setQty(record.getQty());
		response.setDeliveryFee(record.getDeliveryFee());
		response.setTotal(record.getTotal());
		response.setStatus(record.getStatus());
		return response;
	}

	private com.sangareddy.milk.api.dto.SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
		com.sangareddy.milk.api.dto.SubscriptionResponse response = new com.sangareddy.milk.api.dto.SubscriptionResponse();
		response.setMilkType(subscription.getMilkType());
		response.setPrice(subscription.getPrice());
		response.setQty(subscription.getQty());
		response.setActive(subscription.isActive());
		return response;
	}
}
