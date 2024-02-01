package com.example.interviewassignment.repository;

import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

    List<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m JOIN m.borrowedBooks b WHERE b = :book")
    List<Member> findByBorrowedBooks(@Param("book") Books book);

    @Query("SELECT COUNT(b) FROM Member m JOIN m.borrowedBooks b WHERE m = :member")
    int countBorrowedBooks(@Param("member") Member member);

}
