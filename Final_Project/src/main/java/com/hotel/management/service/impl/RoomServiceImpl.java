package com.hotel.management.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.hotel.management.layout.DesignLayouts;
import com.hotel.management.service.RoomService;
import com.hotel.management.util.DBConnectionManager;

public class RoomServiceImpl implements RoomService {

    private final Scanner scanner;

    public RoomServiceImpl() {
        this.scanner = new Scanner(System.in);    //This is a constructor for the RoomServiceImpl class. It initializes the scanner object to read input from the console.
    }

    //SEARCH ROOMS BY TYPE
    @Override
    public List<Integer> searchRoomsByType(String roomType) {
        String selectQuery = "SELECT Room_ID FROM Room WHERE Room_Type = ?";
        List<Integer> roomIDs = new ArrayList<>();
        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, roomType);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIDs.add(resultSet.getInt("Room_ID"));
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
        return roomIDs;
    }

    //SEARCH ROOMS BY PRICE
    @Override
    public List<Integer> searchRoomsByPrice(double price) {
        String selectQuery = "SELECT Room_ID FROM Room WHERE Price <= ?";
        List<Integer> roomIDs = new ArrayList<>();
        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setDouble(1, price);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIDs.add(resultSet.getInt("Room_ID"));
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
        return roomIDs;
    }
    
    //SEARCH ROOMS BY AC STATUS
    @Override
    public List<Integer> searchRoomsByACStatus(String acStatus) {
        String selectQuery = "SELECT Room_ID FROM Room WHERE AC = ?";
        List<Integer> roomIDs = new ArrayList<>();
        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, acStatus);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIDs.add(resultSet.getInt("Room_ID"));  //adds the room ID obtained from the result set to the roomIDs list.
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
        return roomIDs;
    }


    //DISPLAY AVAILABLE ROOMS
    public void displayAvailableRooms() {
        System.out.println("Available Rooms:");
        try (Connection connection = DBConnectionManager.getConnection()) {
            String availabilityQuery = "SELECT Room_ID, Room_Type, Price, AC FROM Room WHERE Availability = 'Available'";
            try (PreparedStatement availabilityStatement = connection.prepareStatement(availabilityQuery)) {
                ResultSet availabilityResult = availabilityStatement.executeQuery();

                while (availabilityResult.next()) {
                    int roomId = availabilityResult.getInt("Room_ID");
                    String roomType = availabilityResult.getString("Room_Type");
                    double price = availabilityResult.getDouble("Price");
                    String acStatus = availabilityResult.getString("AC");
                    System.out.println("Room ID: " + roomId + "\tType: " + roomType + "\tPrice: " + price + "\t AC: " + acStatus + "\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // execute user choice
    public void executeUserChoice() {
        int choice = 0;
        do { 
        	DesignLayouts.printRoomSearchOptions();
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter room type:  (Single ,Double ,Suite)");
                        
                        String roomType = scanner.nextLine();
                        List<Integer> roomIDsByType = searchRoomsByType(roomType);
                        if (roomIDsByType.isEmpty()) {
                            System.out.println("No rooms found for the given type.");
                        } else {
                            System.out.println("Rooms found for type '" + roomType + "': " + roomIDsByType);
                        }
                        break;
                    case 2:
                        System.out.print("Enter the price: (500,1000,2500,5000)");
                        double price = scanner.nextDouble();
                        List<Integer> roomIDsByPrice = searchRoomsByPrice(price);
                        if (roomIDsByPrice.isEmpty()) {
                            System.out.println("No rooms found within the given price range.");
                        } else {
                            System.out.println("Rooms found within price range: " + roomIDsByPrice);
                        }
                        break;
                    case 3:
                        System.out.print("Enter AC status (AC or Non-AC): ");
                        String acStatus = scanner.nextLine();
                        List<Integer> roomIDsByACStatus = searchRoomsByACStatus(acStatus);
                        if (roomIDsByACStatus.isEmpty()) {
                            System.out.println("No rooms found with the given AC status.");
                        } else {
                            System.out.println("Rooms found with AC status '" + acStatus + "': " + roomIDsByACStatus);
                        }
                        break;
                    case 4:
                        displayAvailableRooms();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.err .println("Invalid Input. Please enter a number");
                scanner.next();
            }
        } while (choice != 5);
    }

}
