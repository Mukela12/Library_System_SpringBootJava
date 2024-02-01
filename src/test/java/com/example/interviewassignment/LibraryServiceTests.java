package com.example.interviewassignment;

import com.example.interviewassignment.controller.MemberController;
import com.example.interviewassignment.exceptions.BookNotFoundException;
import com.example.interviewassignment.exceptions.BookNotBorrowedException;
import com.example.interviewassignment.exceptions.MemberNotFoundException;
import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;
import com.example.interviewassignment.models.Books.BookStatus;
import com.example.interviewassignment.repository.BooksRepository;
import com.example.interviewassignment.repository.MemberRepository;
import com.example.interviewassignment.service.LibrarianService;
import com.example.interviewassignment.service.LibrarianServiceImpl;
import com.example.interviewassignment.service.MemberService;
import com.example.interviewassignment.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LibraryServiceTests {

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LibrarianServiceImpl librarianService;

    @InjectMocks
    private MemberServiceImpl memberService;




    // Unit Tests

    // MemberServiceImpl Tests

    @Test
    public void testGetBookById_Success() {
        Books expectedBook = createBook(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(expectedBook));
        Books actualBook = memberService.getBookById(1L);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void testBorrowBook_Success() {
        Books book = createBook(1L);
        Member member = new Member();
        member.setId(2L);
        member.setBorrowedBooks(new HashSet<>(List.of(book)));

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        memberService.borrowBook(1L, member);

        assertTrue(member.getBorrowedBooks().contains(book));
        assertEquals(BookStatus.BORROWED, book.getStatus());
    }

    @Test
    public void testBorrowBook_BookNotFound() {
        when(booksRepository.findById(anyLong())).thenReturn(Optional.empty());

        Member member = new Member();
        assertThrows(BookNotFoundException.class, () -> memberService.borrowBook(1L, member));
    }


    @Test
    public void testBorrowBook_BookNotAvailable() {
        Books book = createBook(1L);
        book.setStatus(BookStatus.BORROWED);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        Member member = new Member();
        assertThrows(BookNotFoundException.class, () -> memberService.borrowBook(1L, member));
    }

    @Test
    public void testReturnBook_Success() {
        Books book = createBook(1L);
        book.setStatus(BookStatus.BORROWED);
        Member member = new Member();
        member.setId(2L);
        member.setBorrowedBooks(new HashSet<>(List.of(book)));

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        memberService.returnBook(1L, member);

        assertFalse(member.getBorrowedBooks().contains(book));
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }


    @Test
    public void testReturnBook_BookNotBorrowed() {
        Books book = createBook(1L);
        book.setStatus(BookStatus.AVAILABLE);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        Member member = new Member();
        assertThrows(BookNotBorrowedException.class, () -> memberService.returnBook(1L, member));
    }

    @Test
    public void testDeleteMember_Success() {
        Member member = new Member();
        member.setId(1L);
        Books book = createBook(1L);

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        member.setBorrowedBooks(new HashSet<>(List.of(book)));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).delete(member);
        when(booksRepository.save(book)).thenReturn(book);

        memberService.deleteMember(1L);

        verify(memberRepository).delete(member);
        verify(booksRepository).save(book);
    }

    @Test
    public void testDeleteMember_MemberNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(1L));
    }

    @Test
    public void testUpdateMember_Success() {
        Member existingMember = new Member();
        existingMember.setId(1L);
        existingMember.setName("Old Name");
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName("New Name");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(existingMember)).thenReturn(existingMember);

        Member result = memberService.updateMember(1L, updatedMember);

        assertEquals("New Name", result.getName());
        verify(memberRepository).save(existingMember);
    }


    @Test
    public void testUpdateMember_MemberNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        Member updatedMember = new Member();
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(1L, updatedMember));
    }

    @Test
    public void testGetAllAvailableBooks_Success() {
        Books book1 = createBook(1L);
        book1.setStatus(BookStatus.AVAILABLE);
        Books book2 = createBook(2L);
        book2.setStatus(BookStatus.BORROWED);

        when(booksRepository.findAvailableBooks()).thenReturn(List.of(book1));

        List<Books> availableBooks = memberService.getAllAvailableBooks();

        assertEquals(1, availableBooks.size());
        assertTrue(availableBooks.contains(book1));
        assertFalse(availableBooks.contains(book2));
    }

    // LibrarianServiceImpl Tests

    @Test
    public void testCreateBook_Success() {
        Books book = createBook(null);

        when(booksRepository.save(book)).thenReturn(book);

        Books createdBook = librarianService.createBook(book);

        System.out.println("existing Book: " + createdBook + "    : Other book " + book);

        assertNotNull(createdBook);

        assertEquals(book.getTitle(), createdBook.getTitle());
    }



    @Test
    public void testUpdateBook_Success() {
        Books book = createBook(1L);

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        when(booksRepository.save(book)).thenReturn(book);

        book.setTitle("Updated Title");

        Books result = librarianService.updateBook(1L, book);

        assertEquals("Updated Title", result.getTitle());
        verify(booksRepository).save(book);
    }

    @Test
    public void testUpdateBook_BookNotFound() {
        when(booksRepository.findById(anyLong())).thenReturn(Optional.empty());

        Books updatedBook = createBook(1L);
        assertThrows(BookNotFoundException.class, () -> librarianService.updateBook(1L, updatedBook));
    }

    @Test
    public void testDeleteBook_Success() {
        Books book = createBook(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));  // Stub to ensure book is found

        librarianService.deleteBook(1L);

        verify(booksRepository).deleteById(1L); // Verify the actual call made by deleteBook
    }


    @Test
    public void testDeleteBook_BookNotFound() {
        when(booksRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> librarianService.deleteBook(1L));
    }


    @Test
    public void testGetAllBooks_Success() {
        Books book1 = createBook(1L);
        Books book2 = createBook(2L);
        when(booksRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Books> allBooks = librarianService.getAllBooks();

        assertEquals(2, allBooks.size());
        assertTrue(allBooks.contains(book1));
        assertTrue(allBooks.contains(book2));
    }


    @Test
    public void testGetBookById_BookNotFound() {
        when(booksRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> librarianService.getBookById(1L));
    }

    @Test
    public void testCreateMember_Success() {
        Member member = createMember(null);

        Member memberWithId = createMember(1L);
        when(memberRepository.save(any(Member.class))).thenReturn(memberWithId);

        Member createdMember = librarianService.createMember(member);

        assertNotNull(createdMember.getId());
        assertEquals(memberWithId.getId(), createdMember.getId());

        assertNotNull(createdMember);
        assertEquals(member.getName(), createdMember.getName());
    }



    private Member createMember(Long id) {
        Member member = new Member();
        if (id != null) {
            member.setId(id);
        }
        member.setName("Test Member");
        member.setUsername("testmember");
        member.setPassword("password");
        member.setEmail("testmember@example.com");
        return member;
    }

    private Books createBook(Long id) {
        Books book = new Books();
        book.setId(id);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        book.setPublicationYear(2023);
        book.setStatus(BookStatus.AVAILABLE);
        return book;
    }

}

