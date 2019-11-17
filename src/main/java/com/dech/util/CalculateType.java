package com.dech.util;

public enum CalculateType {
	INCOME("income"), COSUME("consume"), BENEFIT("benifit"), TOTAL("total");

	private String name;

	CalculateType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
