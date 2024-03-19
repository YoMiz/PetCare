package com.example.app.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetData {
	Integer petId;
	String petName;
	Integer petAge;
	Integer petStatus;
	@DateTimeFormat(pattern="yyyy/MM/dd")
	Date petBirthday;
	String petPhoto;
	@DateTimeFormat(pattern="yyyy/MM/dd")
	Date petCreated;
	@DateTimeFormat(pattern="yyyy/MM/dd")
	Date petUpdated;
	String statusName;
	Integer statusConsumptionFood;
	Integer statusConsumptionSundry;
	Integer statusConsumptionOthers;
}
