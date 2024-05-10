package com.hotel.management.service;

import java.util.List;

public interface RoomService {
    List<Integer> searchRoomsByType(String roomType);
    List<Integer> searchRoomsByPrice(double price);
    //List<Integer> searchAvailableRooms();
	void displayAvailableRooms();
	List<Integer> searchRoomsByACStatus(String isAC);
	
	
}
