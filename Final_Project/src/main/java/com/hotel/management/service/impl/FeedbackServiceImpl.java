package com.hotel.management.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

import com.hotel.management.service.FeedbackService;

public class FeedbackServiceImpl implements FeedbackService {

    private static Scanner scanner = new Scanner(System.in);

    @Override
    public void requestFeedback(String customerEmail, Connection connection) {
        System.out.println("Would you like to provide feedback for your experience? (yes/no)");
        String feedbackResponse = scanner.next().toLowerCase().trim();
        if (feedbackResponse.equals("yes")) {
            System.out.println("Please provide your feedback:");
            scanner.nextLine();
            String feedback = scanner.nextLine();

            // Get Cust_ID corresponding to the provided customerEmail
            int custId = getCustIdByEmail(customerEmail, connection);

            // Save feedback to the database
            saveFeedbackToDatabase(custId, feedback, connection);
        } else {
            System.out.println("Thank you for your feedback. Have a great day!");
        }
    }
    
    //get CustId By Email
    private int getCustIdByEmail(String customerEmail, Connection connection) {
        int custId = -1; // Default value if customer is not found
        String query = "SELECT Cust_ID FROM Customer WHERE Email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, customerEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    custId = resultSet.getInt("Cust_ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer ID: " + e.getMessage());
            e.printStackTrace();
        }
        return custId;
    }
    
    //SAVE FEEDBACK TO DATABASE
    private void saveFeedbackToDatabase(int custId, String feedback, Connection connection) {
        String insertQuery = "INSERT INTO Feedback (Feedback_ID, Cust_ID, Feedback_Message, Feedback_Date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            // Get the maximum feedback_id and increment it by 1
            int feedbackId = getMaxFeedbackId(connection) + 1;
            
            insertStatement.setInt(1, feedbackId);
            insertStatement.setInt(2, custId);
            insertStatement.setString(3, feedback);
            insertStatement.setDate(4, java.sql.Date.valueOf(LocalDate.now())); // Set current local date

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Thank you for your feedback!");
            } else {
                System.out.println("Failed to save feedback. Please try again later.");
            }
        } catch (SQLException e) {
            System.err.println("Error saving feedback to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //GET MAX FEEDBACKID
    private int getMaxFeedbackId(Connection connection) throws SQLException {
        int maxFeedbackId = 0;
        String query = "SELECT MAX(Feedback_ID) AS MaxFeedbackId FROM Feedback";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                maxFeedbackId = resultSet.getInt("MaxFeedbackId");
            }
        }
        return maxFeedbackId;
    }
}
