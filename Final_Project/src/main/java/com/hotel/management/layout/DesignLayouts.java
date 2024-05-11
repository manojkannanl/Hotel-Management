package com.hotel.management.layout;

public class DesignLayouts {
	 public static void displayWelcomeBanner() {
	        System.out.println("********************************************");
	        System.out.println("*                                          *");
	        System.out.println("*      Welcome to the Hotel Management     *");
	        System.out.println("*                  System                  *");
	        System.out.println("*                                          *");
	        System.out.println("********************************************");
	        System.out.println("Are you a customer or an admin or Guest? (Enter 1 for 'customer' or 2 for 'admin' or 3 for Guest or 4 for Exit ):");
	    }
	 
	 public static void displayBookingMenu() {
	        System.out.println("===========================================");
	        System.out.println("|      Welcome to Room Booking System     |");
	        System.out.println("===========================================");
	        System.out.println("| Options:                                |");
	        System.out.println("| 1. Book a room                          |");
	        System.out.println("| 2. Cancel booking                       |");
	        System.out.println("| 3. Refund amount                        |");
	        System.out.println("| 4. Exit                                 |");
	        System.out.println("===========================================");
	        System.out.println("Enter your choice:");
	    }
	 public static void displayPaymentMenu() {
	        System.out.println("===========================================");
	        System.out.println("|        Select Payment Type              |");
	        System.out.println("===========================================");
	        System.out.println("| Options:                                |");
	        System.out.println("| 1. Credit Card                          |");
	        System.out.println("| 2. Cash                                 |");
	        System.out.println("===========================================");
	        System.out.print("Enter your choice: ");
	    }
	 public static void printRoomSearchOptions() {
		    System.out.println("************************************************************");
		    System.out.println("*                      HOTEL MANAGEMENT                    *");
		    System.out.println("*                       ROOM SEARCH                        *");
		    System.out.println("************************************************************");
		    System.out.println("* 1. Search Rooms by Type                                  *");
		    System.out.println("* 2. Search Rooms by Price                                 *");
		    System.out.println("* 3. Search Rooms by AC Status                             *");
		    System.out.println("* 4. Display Available Rooms                               *");
		    System.out.println("* 5. Exit                                                  *");
		    System.out.println("************************************************************");
		}

}
