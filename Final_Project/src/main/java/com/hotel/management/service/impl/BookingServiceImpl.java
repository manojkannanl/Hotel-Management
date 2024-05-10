package com.hotel.management.service.impl;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.hotel.management.layout.DesignLayouts;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.PaymentService;
import com.hotel.management.util.DBConnectionManager;

public class BookingServiceImpl implements BookingService {

	PaymentService paymentService = new PaymentServiceImpl();
	
	private static final String insertQuery = "INSERT INTO Booking (Booking_ID, Room_ID, Cust_ID, Num_Rooms, Check_In, Check_Out, Cancellation_Status, Cancellation_Date, Total_Price) VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), 'not cancelled', NULL, ?)";
	private static final String selectQuery = "SELECT Room_ID FROM Booking WHERE Booking_ID = ?";
	private static final String deleteQuery = "DELETE FROM Booking WHERE Booking_ID = ?";



	Scanner scanner = new Scanner(System.in);
	// int bookingCounter = 1;

	public void chooseOption() {
		boolean exit = false;
		while (!exit) {
			DesignLayouts.displayBookingMenu();
			try {
			int choice = scanner.nextInt();
			switch (choice) {
			case 1:
				bookRoom();
				break;
			case 2:
				cancelBooking();
				break;
			case 3:
				 refundAmount();
				break;
			case 4:
				 exit = true;
				System.out.println("Thank you for using the Room Booking .");
				break;
			default:
				System.out.println("Invalid choice. Please enter a number between 1 and 4.");
			}
		}
		catch(InputMismatchException e) {
        	System.out.println("Invalid Input.Please enter a number");
        	scanner.next();
        	exit=false;
        
		}}
	}

	
	// BOOK ROOM
	public void bookRoom() {
		try {
			System.out.println("Enter Customer ID:");
			int customerId = scanner.nextInt();
			System.out.println("Enter the number of rooms to book:");
			int numRooms = scanner.nextInt();
			double totalPrice = 0.0; // Total price for all rooms
			int lastBookedRoomId = -1; // Initialize to a value that will not conflict with real room IDs
			String roomType = ""; // Initialize room type

			for (int i = 0; i < numRooms; i++) {
				System.out.println("Enter Room ID for room " + (i + 1) + ":");
				int roomId = scanner.nextInt(); // Update roomId

				// Check if the room is available
				String availability = getRoomAvailability(roomId);
				if (availability != null && availability.equals("Available")) {
					// Room is available, add its price to the total
					double roomPrice = getRoomPrice(roomId);
					totalPrice += roomPrice;

					// Get room type
					roomType = getRoomType(roomId);

					// Update room availability
					updateRoomAvailability(roomId, "Unavailable");

					// Update last booked room ID
					lastBookedRoomId = roomId;
				} else {
					// Room is not available or does not exist
					System.out.println("Room with ID " + roomId + " is not available for booking.");
					return;
				}
			}

			// All rooms are available, proceed with booking
			int bookingId = generateBookingId();

			// Get check-in and check-out dates
			System.out.println("Enter check-in date (YYYY-MM-DD):");
			String checkInDate = scanner.next();
			System.out.println("Enter check-out date (YYYY-MM-DD):");
			String checkOutDate = scanner.next();

			// Calculate number of days stayed
			long numDays = ChronoUnit.DAYS.between(LocalDate.parse(checkInDate), LocalDate.parse(checkOutDate));

			// Update total price based on number of days stayed
			totalPrice *= numDays;

			// Insert booking details into the database
			//String insertQuery = "INSERT INTO Booking (Booking_ID, Room_ID, Cust_ID, Num_Rooms, Check_In, Check_Out, Cancellation_Status, Cancellation_Date, Total_Price) VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), 'not cancelled', NULL, ?)";
			try {
				Connection connection = DBConnectionManager.getConnection();
				PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
				insertStatement.setInt(1, bookingId);
				insertStatement.setInt(2, lastBookedRoomId); // Use lastBookedRoomId here
				insertStatement.setInt(3, customerId);
				insertStatement.setInt(4, numRooms);
				insertStatement.setString(5, checkInDate);
				insertStatement.setString(6, checkOutDate);
				insertStatement.setDouble(7, totalPrice);
				int rowsAffected = insertStatement.executeUpdate();

				if (rowsAffected == 1) {

					System.out.println("Room booked successfully. Type: " + roomType + ", Total price: " + totalPrice
							+ ". Booking ID: " + bookingId);
					// Call addPayment method to process payment
					paymentService.addPayment(bookingId, totalPrice, connection);
				}
			} catch (SQLException e) {
				System.err.println("Database Error: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (SQLException e) {
			System.err.println("Database Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	//CANCEL BOOKING
	public void cancelBooking() {
		try (Connection connection = DBConnectionManager.getConnection()) {
			System.out.println("Enter Booking ID to cancel:");
			int bookingId = scanner.nextInt();

			// Retrieve booked rooms and total price
			//String selectQuery = "SELECT Room_ID FROM Booking WHERE Booking_ID = ?";
			try {
				PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
				selectStatement.setInt(1, bookingId);
				ResultSet resultSet = selectStatement.executeQuery();

				while (resultSet.next()) {
					int roomId = resultSet.getInt("Room_ID");
					// Update room availability for each booked room
					updateRoomAvailability(roomId, "Available");
				}
			} catch (SQLException e) {
				System.err.println("Database Error: " + e.getMessage());
				e.printStackTrace();
			}

			// Delete the booking entry
			//String deleteQuery = "DELETE FROM Booking WHERE Booking_ID = ?";
			try {
				PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);

				deleteStatement.setInt(1, bookingId);
				int rowsAffected = deleteStatement.executeUpdate();

				if (rowsAffected == 1) {

					System.out.println("Booking with ID " + bookingId + " cancelled successfully.");
				} else {
					System.out.println("Cancellation failed. Booking ID " + bookingId + " not found.");
				}
			} catch (SQLException e) {
				System.err.println("Database Error: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (SQLException e) {
			System.err.println("Database Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	//REFUND AMOUNT
	public void refundAmount() {
	    try (Connection connection = DBConnectionManager.getConnection()) {
	        System.out.println("Enter Booking ID for refund:");
	        int bookingId = scanner.nextInt();

	        // Retrieve total price for the booking
	        String totalPriceQuery = "SELECT Total_Price FROM Booking WHERE Booking_ID = ?";
	        try (PreparedStatement totalStatement = connection.prepareStatement(totalPriceQuery)) {
	            totalStatement.setInt(1, bookingId);
	            ResultSet totalResult = totalStatement.executeQuery();

	            if (totalResult.next()) {
	                double totalPrice = totalResult.getDouble("Total_Price");
	                
	                // Check if payment has been made for this booking
	                String paymentCheckQuery = "SELECT Payment_Status FROM Payment WHERE Booking_ID = ?";
	                try (PreparedStatement paymentCheckStatement = connection.prepareStatement(paymentCheckQuery)) {
	                    paymentCheckStatement.setInt(1, bookingId);
	                    ResultSet paymentCheckResult = paymentCheckStatement.executeQuery();

	                    if (paymentCheckResult.next()) {
	                        String paymentStatus = paymentCheckResult.getString("Payment_Status");

	                        if (paymentStatus.equals("paid")) {
	                            // Refund the amount
	                            // the refund process involves updating payment status and returning the amount
	                            String refundQuery = "UPDATE Payment SET Payment_Status = 'refunded' WHERE Booking_ID = ?";
	                            try (PreparedStatement refundStatement = connection.prepareStatement(refundQuery)) {
	                                refundStatement.setInt(1, bookingId);
	                                int rowsAffected = refundStatement.executeUpdate();

	                                if (rowsAffected > 0) {
	                                    System.out.println("Refund for Booking ID " + bookingId + " processed successfully.");
	                                    System.out.println("Amount refunded: $" + totalPrice);
	                                } else {
	                                    System.out.println("Refund process failed for Booking ID " + bookingId);
	                                }
	                            }
	                        } else {
	                            System.out.println("No payment has been made for Booking ID " + bookingId + ". Refund not possible.");
	                        }
	                    } else {
	                        System.out.println("No payment information found for Booking ID " + bookingId + ". Refund not possible.");
	                    }
	                }
	            } else {
	                System.out.println("Booking with ID " + bookingId + " not found.");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Database Error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	// Method to generate the next booking ID
	public synchronized int generateBookingId() {
		int bookingId = 0;
		String maxIdQuery = "SELECT MAX(BOOKING_ID) FROM BOOKING";
		try (Connection connection = DBConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(maxIdQuery)) {
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				bookingId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Error generating Customer ID: " + e.getMessage());
			e.printStackTrace();
		}
		return bookingId + 1;
	}

	// Method to display available rooms
	public void displayAvailableRooms() {
		String query = "SELECT Room_ID, Room_Type, Price FROM Room WHERE Availability = 'Available'";
		try (Connection connection = DBConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			ResultSet resultSet = preparedStatement.executeQuery();

			System.out.println("Available Rooms:");
			System.out.println("Room ID\tRoom Type\tPrice");
			while (resultSet.next()) {
				int roomId = resultSet.getInt("Room_ID");
				String roomType = resultSet.getString("Room_Type");
				double price = resultSet.getDouble("Price");
				System.out.println(roomId + "\t\t" + roomType + "\t\t" + price);
			}

		} catch (Exception e) {
			System.err.println("Error displaying available rooms: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getRoomType(int roomId) throws SQLException {
		String query = "SELECT Room_Type FROM Room WHERE Room_ID = ?";
		try (Connection connection = DBConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, roomId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {

				return resultSet.getString("Room_Type");
			} else {
				return null; // Room not found
			}
		}
	}

	private String getRoomAvailability(int roomId) throws SQLException {
		Connection connection = DBConnectionManager.getConnection();
		String query = "SELECT Availability FROM Room WHERE Room_ID = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, roomId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String availability = resultSet.getString("Availability");
				return availability; 
			} else {
				return "Not Found"; // Return a default value if room not found
			}
		}
	}
	
	// GET ROOM PRICE
	private double getRoomPrice(int roomId) throws SQLException {
		Connection connection = DBConnectionManager.getConnection();
		String query = "SELECT Price FROM Room WHERE Room_ID = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, roomId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getDouble("Price");
			} else {
				return 0.0; // Room not found, return default price
			}
		}
	}
	
	// UPDATE ROOM AVAILABILITY
	private void updateRoomAvailability(int roomId, String availability) throws SQLException {
		Connection connection = DBConnectionManager.getConnection(); 
		String updateQuery = "UPDATE Room SET Availability = ? WHERE Room_ID = ?";
		try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
			updateStatement.setString(1, availability);
			updateStatement.setInt(2, roomId);
			int rowsAffected = updateStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Room availability updated successfully for Room ID: " + roomId);
			} else {
				System.out.println("Failed to update room availability for Room ID: " + roomId);
			}
		}
	}
}
