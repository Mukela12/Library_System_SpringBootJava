package com.example.interviewassignment.service;

import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;

import java.util.List;

public interface MemberService {

    List<Books> getAllAvailableBooks();

    Books getBookById(Long id);

    void borrowBook(Long bookId, Member member);

    void returnBook(Long bookId, Member member);

    Member updateMember(Long id, Member member);

    Member getMemberById(Long id);

    void deleteMember(Long id);

}
