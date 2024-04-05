package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.ContactData;

@Mapper
public interface ContactMapper {
	List<ContactData> showContact() throws Exception;
	List<ContactData> showContactForPet(int petId) throws Exception;
	void addContact(ContactData newContactData) throws Exception;
}
