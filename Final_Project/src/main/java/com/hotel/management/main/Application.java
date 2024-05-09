package com.hotel.management.main;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.hotel.management.layout.DesignLayouts;
import com.hotel.management.service.impl.AdminServiceImpl;
import com.hotel.management.service.impl.BookingServiceImpl;
import com.hotel.management.service.impl.CustomerServiceImpl;
import com.hotel.management.service.impl.RoomServiceImpl;
import com.hotel.management.service.CustomerService;
import com.hotel.management.service.RoomService;
import com.hotel.management.service.AdminService;
import com.hotel.management.service.BookingService;

public class Application {
    public static void main(String[] args) {
    	
    	CustomerService customerService = new CustomerServiceImpl();
    	RoomService roomService = new RoomServiceImpl();
    	AdminService adminService = new AdminServiceImpl();
    	BookingService bookingService = new BookingServiceImpl();
    	
        Scanner scanner = new Scanner(System.in);
        
        boolean isLoggedIn = false;

        while (!isLoggedIn) {
        	
            DesignLayouts.displayWelcomeBanner();
            
            System.out.println("******   LOGIN AS  *******");
            System.out.println("1. CUSTOMER");
            System.out.println("2. ADMIN");
            System.out.println("3. GUEST");
            System.out.println("4. EXIT");
            System.out.println("Enter your choice : ");
            try {
                int userTypeChoice = scanner.nextInt();

                switch (userTypeChoice) {
                case 1: // Customer 
                    boolean isCustomerChoiceValid = false;
                    while (!isCustomerChoiceValid) {
                        System.out.println("Do you want to register, login, or go back?");
                        System.out.println("1. REGISTER");
                        System.out.println("2. LOGIN");
                        System.out.println("3. GO BACK");
                        try {
                        	System.out.println("Enter your choice : ");
                            int customerChoice = scanner.nextInt();
                            switch (customerChoice) {
                                case 1: // Register
                                    customerService.registerNewCustomer();
                                    break;
                                case 2: // Login
                                    System.out.println("Enter user name:");
                                    String userName = scanner.next();
                                    System.out.println("Enter password:");
                                    String password = scanner.next();

                                    if (customerService.login(userName, password)) {
                                        System.out.println("Logged in as customer.");

                                        // search
                                        ((RoomServiceImpl) roomService).executeUserChoice();                               

                                        // Proceed to booking
                                        System.out.println("Do you want to book a room? (yes/no)");
                                        String bookChoice = scanner.next();

                                        if (bookChoice.equalsIgnoreCase("yes")) {
                                            bookingService.chooseOption();
                                        } else {
                                            System.out.println("THANK YOU FOR VISITING OUR HOTEL.");
                                        }
                                        isLoggedIn = true; // Set isLoggedIn to true to exit the loop
                                    } else {
                                        System.out.println("Invalid credentials. Please try again.");
                                    }
                                    break;
                                case 3: // Go back
                                    isCustomerChoiceValid = true; // Exit the loop
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.nextLine(); // Consume the invalid input
                        }
                    }
                    break;

                    case 2:
                        System.out.println("Enter admin username:");
                        String adminUsername = scanner.next();
                        System.out.println("Enter admin password:");
                        String adminPassword = scanner.next();

                        if (adminService.adminLogin(adminUsername, adminPassword)) {
                            System.out.println("Admin login successful!");
                            adminService.adminFunctionalities(); // Navigate to admin functionalities
                        } else {
                            System.out.println("Admin login failed. Please try again.");
                        }
                        break;
                        
                        
                    case 3:
                    	 System.out.println("-------WELCOME GUEST-------");
                    	 ((RoomServiceImpl) roomService).executeUserChoice();
                        
                    case 4:
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}
