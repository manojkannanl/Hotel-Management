package com.hotel.management.service;

import java.sql.Connection;

public interface FeedbackService {
    void requestFeedback(String customerEmail, Connection connection);
}
