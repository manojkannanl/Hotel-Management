package com.hotel.management.service.impl;

import java.sql.*;
import java.util.Scanner;

import com.hotel.management.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {

    static Scanner scanner = new Scanner(System.in);

    // method to addPayment
    public void addPayment(int bookingId, double amountPaid, Connection connection) {
        try {
            int paymentId = generatePaymentId(connection);
            String paymentStatus = "paid";
            String customerEmail = getCustomerEmail(bookingId, connection);

            displayPaymentMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addCreditCardPayment(paymentId, bookingId, amountPaid, paymentStatus, connection);
                    break;
                case 2:
                    addCashPayment(paymentId, bookingId, amountPaid, paymentStatus, connection);
                    break;
                default:
                    System.out.println("Invalid choice. Defaulting to Credit Card.");
                    addCreditCardPayment(paymentId, bookingId, amountPaid, paymentStatus, connection);
            }

            sendNotificationToCustomer(bookingId, customerEmail, connection);
            
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // send notification to customer
    private void sendNotificationToCustomer(int bookingId, String customerEmail, Connection connection) {
        try {
            String bookingDetails = getBookingDetailsFromDatabase(bookingId, connection);
            System.out.println("Sending notification to customer at " + customerEmail);
            System.out.println("Booking details for Booking ID: " + bookingId);
            System.out.println(bookingDetails);

            // Request feedback after sending notification
            FeedbackServiceImpl feedbackService = new FeedbackServiceImpl();
            feedbackService.requestFeedback(customerEmail, connection);
        } catch (Exception e) {
            System.err.println("Error sending notification to customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // getbooking details from database
    private String getBookingDetailsFromDatabase(int bookingId, Connection connection) throws Exception {
        String query = "SELECT * FROM Booking WHERE Booking_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int roomId = resultSet.getInt("Room_ID");
                int customerId = resultSet.getInt("Cust_ID");
                int numRooms = resultSet.getInt("Num_Rooms");
                Date checkInDate = resultSet.getDate("Check_In");
                Date checkOutDate = resultSet.getDate("Check_Out");
                double totalPrice = resultSet.getDouble("Total_Price");
                String roomType = getRoomType(roomId, connection);
                System.out.println("\n");
                System.out.println("# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #");
                System.out.println("-----CUSTOMER BOOKED DETAILS-----");
                System.out.println("# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #");
                return "Room ID: " + roomId + "\n"
                        + "Customer ID: " + customerId + "\n"
                        + "Number of Rooms: " + numRooms + "\n"
                        + "Check-in Date: " + checkInDate.toLocalDate() + "\n"
                        + "Check-out Date: " + checkOutDate.toLocalDate() + "\n"
                        + "Total Price: $" + totalPrice + "\n"
                        + "Room Type: " + roomType + "\n";
            } else {
                throw new Exception("Booking with ID " + bookingId + " not found.");
            }
        }
    }


    private String getCustomerEmail(int bookingId, Connection connection) throws SQLException {
        String query = "SELECT Email FROM Customer WHERE Cust_ID = (SELECT Cust_ID FROM Booking WHERE Booking_ID = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Email");
            } else {
                throw new SQLException("Customer email not found for Booking ID: " + bookingId);
            }
        }
    }

    //get room type
    private String getRoomType(int roomId, Connection connection) throws SQLException {
        String query = "SELECT Room_Type FROM Room WHERE Room_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Room_Type");
            } else {
                throw new SQLException("Room type not found for Room ID: " + roomId);
            }
        }
    }
    
    // generate  by payment id
    private int generatePaymentId(Connection connection) throws SQLException {
        int paymentId = 0;
        String query = "SELECT MAX(Payment_ID) AS MaxPaymentId FROM Payment";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                paymentId = resultSet.getInt("MaxPaymentId") + 1;
            }
        }
        return paymentId;
    }
    
    
    //add credit card payment
    private void addCreditCardPayment(int paymentId, int bookingId, double amountPaid, String paymentStatus, Connection connection) throws SQLException {
      
        String insertQuery = "INSERT INTO Payment (Payment_ID, Booking_ID, Amount_Paid, Payment_Type, Payment_Status) VALUES (?, ?, ?, 'Credit Card', ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, paymentId);
            insertStatement.setInt(2, bookingId);
            insertStatement.setDouble(3, amountPaid);
            insertStatement.setString(4, paymentStatus);
            int rowsAffected = insertStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Credit card payment added successfully.");
                updatePaymentStatus(bookingId, "paid", connection);
                updateBookingStatus(bookingId, "booked", connection); // Update booking status to booked
            } else {
                System.out.println("Failed to add credit card payment.");
            }
        }
    }

    // add cash payment
    private void addCashPayment(int paymentId, int bookingId, double amountPaid, String paymentStatus, Connection connection) throws SQLException {
        // Here you implement the logic to add cash payment to the database
        String insertQuery = "INSERT INTO Payment (Payment_ID, Booking_ID, Amount_Paid, Payment_Type, Payment_Status) VALUES (?, ?, ?, 'Cash', ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, paymentId);
            insertStatement.setInt(2, bookingId);
            insertStatement.setDouble(3, amountPaid);
            insertStatement.setString(4, paymentStatus);
            int rowsAffected = insertStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Cash payment added successfully.");
                updatePaymentStatus(bookingId, "paid", connection);
                updateBookingStatus(bookingId, "booked", connection); // Update booking status to booked
            } else {
                System.out.println("Failed to add cash payment.");
            }
        }
    }

    // Method to update payment status
    private void updatePaymentStatus(int bookingId, String paymentStatus, Connection connection) {
        try {
            String updateQuery = "UPDATE Payment SET Payment_Status = ? WHERE Booking_ID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, paymentStatus);
                updateStatement.setInt(2, bookingId);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Payment status updated successfully to 'paid' for Booking ID: " + bookingId);
                } else {
                    System.out.println("Failed to update payment status for Booking ID: " + bookingId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to update booking status
    private void updateBookingStatus(int bookingId, String bookingStatus, Connection connection) {
        try {
            String updateQuery = "UPDATE Booking SET Booking_Status = ? WHERE Booking_ID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, bookingStatus);
                updateStatement.setInt(2, bookingId);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Booking status updated successfully to 'booked' for Booking ID: " + bookingId);
                } else {
                    System.out.println("Failed to update booking status for Booking ID: " + bookingId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //display payment Menu
    private void displayPaymentMenu() {
        System.out.println("Choose payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Cash");
        System.out.print("Enter your choice: ");
    }
}
