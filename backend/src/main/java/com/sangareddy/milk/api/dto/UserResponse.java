package com.sangareddy.milk.api.dto;

public class UserResponse {
	private Long id;
	private String name;
	private int walletBalance;
	private int deliveryFee;
	private String deliveryArea;
	private SubscriptionResponse subscription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(int walletBalance) {
		this.walletBalance = walletBalance;
	}

	public int getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(int deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	public String getDeliveryArea() {
		return deliveryArea;
	}

	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}

	public SubscriptionResponse getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionResponse subscription) {
		this.subscription = subscription;
	}
}
