package com.example.interviewassignment.exceptions; // Adjust the package as needed

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}

