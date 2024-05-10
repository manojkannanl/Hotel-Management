package com.hotel.management.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.hotel.management.exception.InputInvalidException;
import com.hotel.management.exception.RoomNotFoundException;
import com.hotel.management.service.AdminService;
import com.hotel.management.util.DBConfig;

public class AdminServiceImpl implements AdminService {

    static Scanner scanner = new Scanner(System.in);

    private static final String selectQuery = "SELECT * FROM Admin WHERE Username = ? AND Password = ?";
    private static final String insertQuery = "INSERT INTO Room (Room_ID, Room_Type, Price, Availability, AC) VALUES (?, ?, ?, ?, ?)";
    private static final String deleteQuery = "DELETE FROM Room WHERE Room_ID = ?";
    private static final String updateQuery = "UPDATE Room SET Availability = 'Booked' WHERE Room_ID = ?";

    public boolean adminLogin(String adminUsername, String adminPassword) {
        try (Connection connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, adminUsername);
            preparedStatement.setString(2, adminPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If a row is returned, the login is successful
            }

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            return false; // Return false in case of an exception
        }
    }

    public void adminFunctionalities() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Admin Functionalities:");
            System.out.println("1. Add Room");
            System.out.println("2. Remove Room");
            System.out.println("3. Update Room Details");
            System.out.println("4. View Room Details");
            System.out.println("5. Book Room");
            System.out.println("6. BookingHistory");
            System.out.println("7. Exit");
            System.out.println("Enter your choice:");
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        addRoom();
                        break;
                    case 2:
                        removeRoom();
                        break;
                    case 3:
                        updateRoom();
                        break;
                    case 4:
                        viewRoomDetails();
                        break;
                    case 5:
                        bookRoom();
                        break;
                    case 6:
                        viewBookingHistory();
                        break;
                    case 7:
                        exit = true;
                        System.out.println("Exiting admin functionalities...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input.Please enter a number");
                scanner.next();
            }
        }
    }

    // ADD ROOM
    private void addRoom() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            System.out.println("-".repeat(80));
            System.out.println("Do you want to view room details? (yes/no):");
            String viewDetailsOption = scanner.next().toLowerCase();
            if (viewDetailsOption.equals("yes")) {
                // Call view room details method here
                viewRoomDetails();
            }

           // System.out.println("Enter Room ID:");
            int roomId = getValidation(scanner);
            System.out.println("Enter Room Type:");
            String roomType = scanner.next();
            System.out.println("Enter Price:");
            double price = scanner.nextDouble();
            System.out.println("Enter Availability (Available/Unavailable):");
            String availability = scanner.next();
            System.out.println("Enter AC Status (AC/Non-AC):");
            String acStatus = scanner.next();
            System.out.println("-".repeat(80));

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, roomId);
                insertStatement.setString(2, roomType);
                insertStatement.setDouble(3, price);
                insertStatement.setString(4, availability);
                insertStatement.setString(5, acStatus);
                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room added successfully.");
                } else {
                    System.out.println("Failed to add room.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    //REMOVE ROOM 
    private void removeRoom() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            System.out.println("Do you want to view room details? (yes/no):");
            String viewDetailsOption = scanner.next().toLowerCase();
            if (viewDetailsOption.equals("yes")) {
                // Call view room details method here
                viewRoomDetails();
            }

            System.out.println("Enter Room ID to remove:");
            int roomId = getIntInput();

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, roomId);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room removed successfully.");
                } else {
                    throw new RoomNotFoundException("Failed to remove room. Room ID not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error removing room: " + e.getMessage());
        } catch (RoomNotFoundException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    private int getIntInput() {
        int input;
        while (true) {
            try {
                input = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer:");
                scanner.nextLine(); // Consume the invalid input
            }
        }
        return input;
    }

    
    //UPDATE ROOM
    private void updateRoom() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            System.out.println("Do you want to view room details? (yes/no):");
            String viewDetailsOption = scanner.next().toLowerCase();
            if (viewDetailsOption.equals("yes")) {
                // Call view room details method here
                viewRoomDetails();
            }

            System.out.println("Enter Room ID to update:");
            int roomId = scanner.nextInt();
            System.out.println("Enter new Room Type:");
            String roomType = scanner.next();
            System.out.println("Enter new Price:");
            double price = scanner.nextDouble();
            System.out.println("Enter new Availability (Available/Unavailable):");
            String availability = scanner.next();
            System.out.println("Enter new Air Conditioning Status (AC/Non-AC):");
            String acStatus = scanner.next();

            String updateQuery = "UPDATE Room SET Room_Type = ?, Price = ?, Availability = ?, AC = ? WHERE Room_ID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, roomType);
                updateStatement.setDouble(2, price);
                updateStatement.setString(3, availability);
                updateStatement.setString(4, acStatus);
                updateStatement.setInt(5, roomId);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room details updated successfully.");
                } else {
                    System.out.println("Failed to update room details. Room ID not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating room details: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    //VIEW ROOM DETAILS
    public void viewRoomDetails() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            System.out.println("Room Details:");
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Room");
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Room ID\t\tRoom Type\tPrice\t\tAvailability\t\tAC");
                while (resultSet.next()) {
                    int roomId = resultSet.getInt("Room_ID");
                    String roomType = resultSet.getString("Room_Type");
                    double price = resultSet.getDouble("Price");
                    String availability = resultSet.getString("Availability");
                    String acStatus = resultSet.getString("AC");
                    System.out.println(roomId + "\t\t" + roomType + "\t\t" + price + "\t\t" + availability + "\t\t" + acStatus);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing room details: " + e.getMessage());
        } finally {	
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    
    // BOOK ROOM
    private void bookRoom() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            System.out.println("Do you want to view room details? (yes/no):");
            String viewDetailsOption = scanner.next().toLowerCase();
            if (viewDetailsOption.equals("yes")) {
                // Call view room details method here
                viewRoomDetails();
            }

            System.out.println("Enter Room ID to book:");
            int roomId = scanner.nextInt();

            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, roomId);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room booked successfully.");
                } else {
                    System.out.println("Failed to book room. Room ID not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error booking room: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    
    //VIEW BOOKING HISTORY
    private void viewBookingHistory() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT Booking_ID, Room_ID, Cust_ID, Num_Rooms, TO_CHAR(Check_In, 'YYYY-MM-DD') AS Check_In_Date, TO_CHAR(Check_Out, 'YYYY-MM-DD') AS Check_Out_Date, Total_Price FROM Booking");
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Booking ID\tRoom ID\t\tNum Rooms\tCheck In\t\tCheck Out\t\tTotal Price");
                while (resultSet.next()) {
                    int bookingId = resultSet.getInt("Booking_ID");

                    int roomId = resultSet.getInt("Room_ID");
                    System.out.println("\t\t");
                    int numRooms = resultSet.getInt("Num_Rooms");
                    String checkInDate = resultSet.getString("Check_In_Date");
                    String checkOutDate = resultSet.getString("Check_Out_Date");
                    double totalPrice = resultSet.getDouble("Total_Price");
                    System.out.println(bookingId + "\t\t" + roomId + "\t\t" + numRooms + "\t\t" + checkInDate + "\t\t" + checkOutDate + "\t\t" + totalPrice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing booking history: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    private static int getValidation(Scanner sc) {
    	System.out.println("Enter the room number");
    	int id=sc.nextInt();
    	if(id>=100 && id<=120)
			return id;
		else
			System.out.println("Invalid room number");
			return getValidation(sc);
    }
    aas
}
