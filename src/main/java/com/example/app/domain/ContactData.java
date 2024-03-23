package com.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactData {
	Integer contactId;
	Integer petId;
	Integer contactType;
	String contactName;
	String contactInCharge;
	String contactTel;
	String contactMail;
	String contactUrl;
	String contactTypeName;
	
}
