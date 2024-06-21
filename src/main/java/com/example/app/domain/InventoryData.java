package com.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryData {
	Integer inventoryId;
	Integer userId;
	Integer petId;
	Integer inventoryType;
	String inventoryName;
	String inventoryTypeName;
	Integer inventoryAmount;
	Integer inventoryPrice;
	String inventoryUrl;
	Integer inventoryPetId;
	Integer inventoryPetStatus;
	
	
}
