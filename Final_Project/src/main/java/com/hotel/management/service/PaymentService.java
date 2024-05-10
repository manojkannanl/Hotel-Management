package com.hotel.management.service;

import java.sql.Connection;

public interface PaymentService {

	void addPayment(int bookingId, double totalPrice, Connection connection);

}
