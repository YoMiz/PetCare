package com.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryData {
	Integer inventoryId;
	Integer petId;
	Integer inventoryType;
	String inventoryName;
	String inventoryTypeName;
	Integer inventoryAmount;
	Integer inventoryPrice;
}
