package com.sangareddy.milk.api;

import com.sangareddy.milk.api.dto.DeliveryRequest;
import com.sangareddy.milk.api.dto.LoginRequest;
import com.sangareddy.milk.api.dto.OrderResponse;
import com.sangareddy.milk.api.dto.SimulationResponse;
import com.sangareddy.milk.api.dto.SubscriptionRequest;
import com.sangareddy.milk.api.dto.SubscriptionResponse;
import com.sangareddy.milk.api.dto.UserResponse;
import com.sangareddy.milk.api.dto.WalletRequest;
import com.sangareddy.milk.model.OrderRecord;
import com.sangareddy.milk.model.Subscription;
import com.sangareddy.milk.model.User;
import com.sangareddy.milk.repo.OrderRecordRepository;
import com.sangareddy.milk.repo.SubscriptionRepository;
import com.sangareddy.milk.repo.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserRepository userRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final OrderRecordRepository orderRecordRepository;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);

	public UserController(UserRepository userRepository, SubscriptionRepository subscriptionRepository,
			OrderRecordRepository orderRecordRepository) {
		this.userRepository = userRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.orderRecordRepository = orderRecordRepository;
	}

	@PostMapping("/login")
	public UserResponse login(@Valid @RequestBody LoginRequest request) {
		String name = request.getName().trim();
		User user = userRepository.findByNameIgnoreCase(name).orElseGet(() -> {
			User created = new User();
			created.setName(name);
			created.setWalletBalance(0);
			return userRepository.save(created);
		});
		Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
		return toUserResponse(user, subscription);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
		return userRepository.findById(id)
				.map(user -> ResponseEntity.ok(toUserResponse(user,
						subscriptionRepository.findByUserId(user.getId()).orElse(null))))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping("/{id}/wallet/recharge")
	public ResponseEntity<UserResponse> recharge(@PathVariable Long id, @Valid @RequestBody WalletRequest request) {
		return userRepository.findById(id).map(user -> {
			user.setWalletBalance(user.getWalletBalance() + request.getAmount());
			userRepository.save(user);
			Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
			return ResponseEntity.ok(toUserResponse(user, subscription));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping("/{id}/subscription")
	public ResponseEntity<SubscriptionResponse> subscribe(@PathVariable Long id,
			@Valid @RequestBody SubscriptionRequest request) {
		return userRepository.findById(id).map(user -> {
			Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(new Subscription());
			subscription.setUser(user);
			subscription.setMilkType(request.getMilkType());
			subscription.setPrice(request.getPrice());
			subscription.setQty(request.getQty());
			subscription.setActive(true);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok(toSubscriptionResponse(subscription));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PutMapping("/{id}/delivery")
	public ResponseEntity<UserResponse> updateDelivery(@PathVariable Long id,
			@Valid @RequestBody DeliveryRequest request) {
		return userRepository.findById(id).map(user -> {
			user.setDeliveryArea(request.getArea());
			user.setDeliveryFee(request.getFee());
			userRepository.save(user);
			Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
			return ResponseEntity.ok(toUserResponse(user, subscription));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping("/{id}/simulate")
	public ResponseEntity<SimulationResponse> simulate(@PathVariable Long id) {
		User user = userRepository.findById(id).orElse(null);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
		if (subscription == null || !subscription.isActive()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		int total = subscription.getPrice() * subscription.getQty() + user.getDeliveryFee();
		String status;
		String message;
		if (user.getWalletBalance() < total) {
			status = "Failed - Low Balance";
			message = "Low Balance";
		} else {
			status = "Delivered";
			message = "Delivered! Deducted ₹" + total;
			user.setWalletBalance(user.getWalletBalance() - total);
			userRepository.save(user);
		}
		OrderRecord record = new OrderRecord();
		record.setUser(user);
		record.setOrderDate(LocalDate.now());
		record.setItem(subscription.getMilkType());
		record.setQty(subscription.getQty());
		record.setDeliveryFee(user.getDeliveryFee());
		record.setTotal(total);
		record.setStatus(status);
		orderRecordRepository.save(record);

		SimulationResponse response = new SimulationResponse();
		response.setStatus(status);
		response.setTotal(total);
		response.setWalletBalance(user.getWalletBalance());
		response.setMessage(message);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}/orders")
	public ResponseEntity<List<OrderResponse>> orders(@PathVariable Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		List<OrderResponse> response = orderRecordRepository.findByUserIdOrderByCreatedAtDesc(id).stream()
				.map(this::toOrderResponse)
				.toList();
		return ResponseEntity.ok(response);
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

	private SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
		SubscriptionResponse response = new SubscriptionResponse();
		response.setMilkType(subscription.getMilkType());
		response.setPrice(subscription.getPrice());
		response.setQty(subscription.getQty());
		response.setActive(subscription.isActive());
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
}
