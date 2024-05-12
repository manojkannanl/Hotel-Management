package com.hotel.management.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

import com.hotel.management.exception.InputInvalidException;

public class ValidationUtil {
    static Scanner scanner = new Scanner(System.in);

    public static String getValidEmail(Connection connection) {
        while (true) {
            System.out.println("Enter Email:");
            String email = scanner.next();
            
            if (ValidationUtil.isValidEmail(email) && !isEmailExists(email, connection)) {
                return email;
            } else {
               try {
            	   throw new InputInvalidException("Invalid Email. Please enter a valid email address that does not already exist in the database.");
               } catch(InputInvalidException e) {
            	   System.out.println(e.getMessage());
            	   getValidEmail(connection);
               }
            }
        }
    }

    private static boolean isEmailExists(String email, Connection connection) {
        String query = "SELECT COUNT(*) FROM Customer WHERE Email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if email exists: " + e.getMessage());
        }
        return false;
    }


    public static String getValidPassword() throws InputInvalidException {
        System.out.println("Password should be at least 8 characters and include lowercase, uppercase, digit, and special character.");
        while (true) {
            System.out.println("Enter Password:");
            String password = scanner.next();
            if (isValidPassword(password)) {
                return password;
            } else {
                throw new InputInvalidException("Invalid Password. Password should be at least 8 characters and include lowercase, uppercase, digit, and special character.");
            }
        }
    }

    public static String getValidContactNumber() throws InputInvalidException {
        System.out.println("Please enter a 10-digit number starting with a digit greater than 5");
        while (true) {
            System.out.println("Enter Contact Number:");
            String contactNumber = scanner.next();
            if (isValidContactNumber(contactNumber)) {
                return contactNumber;
            } else {
                throw new InputInvalidException("Invalid Contact Number. Please enter a 10-digit number starting with a digit greater than 5");
            }
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-+]).{8,}$");
    }

    public static boolean isValidContactNumber(String contactNumber) {
        return contactNumber.matches("[6-9]\\d{9}");
    }

    public static boolean isValidAadharNumber(String aadharNumber) throws InputInvalidException {
        // Aadhar number must be exactly 12 digits
        if (aadharNumber.length() != 12) {
            throw new InputInvalidException("Invalid Aadhar Number. It must be exactly 12 digits.");
        }

        // Aadhar number must contain only digits
        for (char c : aadharNumber.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new InputInvalidException("Invalid Aadhar Number. It must contain only digits.");
            }
        }

        return true;
    }
    public static String getValidFirstName() {
        while (true) {
            try {
                System.out.println("Enter First Name:");
                String firstName = scanner.next();
                if (isValidName(firstName)) {
                    return firstName;
                } else {
                    System.out.println("Invalid First Name. First name must contain only alphabetic characters.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. First name must contain only alphabetic characters.");
                scanner.next(); // Clear the invalid input from the scanner
            }
        }
    }

    public static String getValidLastName() {
        while (true) {
            try {
                System.out.println("Enter Last Name:");
                String lastName = scanner.next();
                if (isValidName(lastName)) {
                    return lastName;
                } else {
                    System.out.println("Invalid Last Name. Last name must contain only alphabetic characters.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Last name must contain only alphabetic characters.");
                scanner.next(); // Clear the invalid input from the scanner
            }
        }
    }
    
//    public static Date getValidDOB() {
//        while (true) {
//            try {
//                System.out.println("Enter Date of Birth (YYYY-MM-DD):");
//                String dobString = scanner.next();
//                return Date.valueOf(dobString);
//            } catch (IllegalArgumentException e) {
//                System.out.println("Invalid Date of Birth format. Please enter date in the format YYYY-MM-DD.");
//            }
//        }
//    }
    

   
        public static Date getValidDOB() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            
            while (true) {
                try {
                    System.out.println("Enter Date of Birth (YYYY-MM-DD):");
                    String dobString = scanner.next();
                    
                    Date dob = dateFormat.parse(dobString);
                    return dob;
                } catch (ParseException e) {
                    System.out.println("Invalid Date of Birth format. Please enter date in the format YYYY-MM-DD.");
                }
            }
        }
    



    public static boolean isValidDOBFormat(String dobString) {
        // Check if the input matches the expected format (YYYY-MM-DD)
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        return dobString.matches(regex);
    }
    
    public static boolean isValidName(String name) {
        // Regular expression to check if the name contains only letters
        return name.matches("[a-zA-Z]+");
    }
    
    

    public static class AgeCalculator {
        public static int calculateAge(LocalDate string) {
            LocalDate currentDate = LocalDate.now();
            return Period.between(string, currentDate).getYears();
        }
    }


}
