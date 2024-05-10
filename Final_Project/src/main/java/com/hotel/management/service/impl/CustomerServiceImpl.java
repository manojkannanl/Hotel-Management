package com.hotel.management.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

import com.hotel.management.model.Customer;
import com.hotel.management.service.CustomerService;
import com.hotel.management.util.DBConfig;
import com.hotel.management.util.DBConnectionManager;
import com.hotel.management.util.ValidationUtil;

public class CustomerServiceImpl implements CustomerService {
    static Scanner scanner = new Scanner(System.in);

    public void registerNewCustomer() {
        // Get customer details with validation
        int custId = generateCustomerId(); // Generate Customer ID

        String aadharNumber;

        try {
            // Validate Aadhar Number
            do {
                System.out.println("Enter Aadhar Number:");
                aadharNumber = scanner.nextLine();
                if (!ValidationUtil.isValidAadharNumber(aadharNumber)) {
                    System.out.println("Invalid Aadhar Number. Aadhar number must be exactly 12 digits and contain only digits.");
                }
            } while (!ValidationUtil.isValidAadharNumber(aadharNumber));

            // Validate First Name
            String firstName = ValidationUtil.getValidFirstName();

            // Validate Last Name
            String lastName = ValidationUtil.getValidLastName();

            // Validate Address
            System.out.println("Enter Address:");
            String address = scanner.nextLine();

            // Validate Gender
            String gender = "";
            boolean isValidGender = false;

            while (!isValidGender) {
                System.out.println("Select Gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        gender = "male";
                        isValidGender = true;
                        break;
                    case 2:
                        gender = "female";
                        isValidGender = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1 for 'male' or 2 for 'female'.");
                }
            }

     
            // Validate Date of Birth
            java.util.Date utilDOB = ValidationUtil.getValidDOB();
            java.sql.Date dob = new java.sql.Date(utilDOB.getTime()); // Convert util.Date to sql.Date

           


            int age = ValidationUtil.AgeCalculator.calculateAge(dob.toLocalDate());
            System.out.println("Age: " + age);


            // Validate Contact Number
            String contactNumber = ValidationUtil.getValidContactNumber();

            // Validate Email
            Connection connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
            String email = ValidationUtil.getValidEmail(connection);


            // Validate User Name
            System.out.println("Enter User Name:");
            String userName = scanner.next();

            // Validate Password
            String password = ValidationUtil.getValidPassword();

            // Create Customer object
            Customer customer = new Customer();
            customer.setCustId(custId);
            customer.setAadharNo(aadharNumber);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddress(address);
            customer.setGender(gender);
            customer.setDob(dob);
            customer.setContactNo(contactNumber);
            customer.setEmail(email);
            customer.setUserName(userName);
            customer.setPassword(password);

            // Save customer details
            saveCustomerDetails(customer);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void saveCustomerDetails(Customer customer) {

        String insertQuery = "INSERT INTO Customer (Cust_ID, AADHAR_NUMBER, First_Name, Last_Name, Address, Gender, DOB, Contact_No, Email, User_Name, Password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            // Set parameters in the prepared statement
            preparedStatement.setInt(1, customer.getCustId());
            preparedStatement.setString(2, customer.getAadharNo());
            preparedStatement.setString(3, customer.getFirstName());
            preparedStatement.setString(4, customer.getLastName());
            preparedStatement.setString(5, customer.getAddress());
            preparedStatement.setString(6, customer.getGender());
            preparedStatement.setDate(7, customer.getDob());
            preparedStatement.setString(8, customer.getContactNo());
            preparedStatement.setString(9, customer.getEmail());
            preparedStatement.setString(10, customer.getUserName());
            preparedStatement.setString(11, customer.getPassword());

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("New Customer Registered Successfully. Customer ID: " + customer.getCustId());
            } else {
                System.out.println("Failed to register the new customer.");
            }
        } catch (SQLException e) {
            System.err.println("Error saving customer details: " + e.getMessage());
        }
    }

    private static int generateCustomerId() {
        int customerId = 0;
        String maxIdQuery = "SELECT MAX(Cust_ID) FROM Customer";
        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(maxIdQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customerId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error generating Customer ID: " + e.getMessage());
        }
        return customerId + 1;
    }

    public boolean login(String userName, String password) {

        // SQL query to check user_name and Passwprd.
        String selectQuery = "SELECT * FROM Customer WHERE User_Name = ? AND Password = ?";

        try (Connection connection = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login Successful!");
                return true;
            } else {
                System.out.println("Invalid credentials. Login failed.");
                return false;
            }
        } catch (SQLException e) {  
            System.err.println("Database Error: " + e.getMessage());
            return false;
        }
    }
}
