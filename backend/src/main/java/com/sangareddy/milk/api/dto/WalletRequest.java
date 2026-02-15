package com.sangareddy.milk.api.dto;

import jakarta.validation.constraints.Min;

public class WalletRequest {
	@Min(1)
	private int amount;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
