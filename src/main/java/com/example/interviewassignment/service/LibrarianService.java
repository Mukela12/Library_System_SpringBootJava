package com.example.interviewassignment.service;

import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;

import java.util.List;

public interface LibrarianService {

    Books createBook(Books book);

    Books updateBook(Long id, Books book);

    void deleteBook(Long id);

    List<Books> getAllBooks();

    Books getBookById(Long id);

    // Member Management Methods

    Member createMember(Member member);

    Member updateMember(Long id, Member member);

    void deleteMember(Long id);

    List<Member> getAllMembers();

    Member getMemberById(Long id);

    // Additional methods, if any

}
