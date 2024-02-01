package com.example.interviewassignment.repository;

import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BooksRepository extends JpaRepository<Books, Long> {

    @Query("SELECT b FROM Books b WHERE b.title LIKE :title%")
    List<Books> findByTitleLike(@Param("title") String title);

    @Query("SELECT b FROM Books b WHERE b.title LIKE ?1 OR b.author LIKE ?2")
    List<Books> findByTitleOrAuthor(String title, String author);

    @Query("SELECT b FROM Books b WHERE b.status = 'AVAILABLE'")
    List<Books> findAvailableBooks();

}
