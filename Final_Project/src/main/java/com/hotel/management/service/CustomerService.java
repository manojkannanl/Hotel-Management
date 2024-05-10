package com.hotel.management.service;

public interface CustomerService {

	void registerNewCustomer();

	boolean login(String userName, String password);

}
