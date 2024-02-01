package com.example.interviewassignment.exceptions; // Adjust package path as needed

public class BookNotBorrowedException extends RuntimeException {

    public BookNotBorrowedException(String message) {
        super(message);
    }
}

