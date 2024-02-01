package com.example.interviewassignment;

import com.example.interviewassignment.controller.MemberController;
import com.example.interviewassignment.exceptions.BookNotFoundException;
import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Books.BookStatus;
import com.example.interviewassignment.models.Member;
import com.example.interviewassignment.repository.BooksRepository;
import com.example.interviewassignment.service.LibrarianService;
import com.example.interviewassignment.repository.MemberRepository;
import com.example.interviewassignment.service.LibrarianServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksRepository booksRepository;

    @MockBean
    private MemberRepository memberRepository; // Assuming member interactions

    @InjectMocks
    private LibrarianServiceImpl librarianService;

    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    public void testReturnBook_Integration() throws Exception {

        Books book1 = createBook(1L);
        book1.setStatus(BookStatus.BORROWED);

        Member member = createMember(2L);
        member.setBorrowedBooks(new HashSet<>(List.of(book1)));

        when(booksRepository.findById(book1.getId())).thenReturn(Optional.of(book1));
        when(memberRepository.save(member)).thenReturn(member);

        mockMvc.perform(post("/member/return/" + book1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(member)))
                .andExpect(status().isNoContent());

        assertThat(book1.getStatus()).isEqualTo(BookStatus.AVAILABLE); // Verify book status updated
    }




    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    public void testGetAllAvailableBooks_Integration() throws Exception {
        Books availableBook1 = createBook(1L);
        Books availableBook2 = createBook(2L);
        List<Books> availableBooks = List.of(availableBook1, availableBook2);

        when(booksRepository.findAvailableBooks()).thenReturn(availableBooks);

        mockMvc.perform(get("/member/books"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L));
    }




    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    public void testGetBookById_Integration() throws Exception {
        Books book = createBook(1L);

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/member/books/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }



    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    public void testDeleteAccount_Integration() throws Exception {
        Member member = createMember(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        mockMvc.perform(delete("/member/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToObjects(member)))
                .andExpect(status().isNoContent());

        verify(memberRepository).delete(member);
    }


    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    public void testBorrowBook_Integration() throws Exception {
        Books book = createBook(1L);
        book.setStatus(BookStatus.AVAILABLE);

        Member member = createMember(2L);

        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(post("/member/borrow/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(member)))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(booksRepository).save(book);

        assertThat(book.getStatus()).isEqualTo(BookStatus.BORROWED);
        assertThat(member.getBorrowedBooks().contains(book));
    }


    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")  // Set user details using annotation
    public void testLogin_Integration() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param("username", "librarian")
                        .param("password", "librarianpassword"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/librarian"));
    }


    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")
    public void testGetAllBooks() throws Exception {
        List<Books> expectedBooks = List.of(createBook(1L), createBook(2L));
        when(booksRepository.findAll()).thenReturn(expectedBooks);

        MvcResult result = mockMvc.perform(get("/librarian/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        List<Books> actualBooks = convertToObject(responseBody, new TypeReference<>() {});

        assertThat(actualBooks).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedBooks);
    }

    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")
    public void testCreateBook() throws Exception {
        Books newBook = createBook(1L);

        String jsonBook = convertToJson(newBook);

        mockMvc.perform(post("/librarian/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBook))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")
    public void testDeleteBook() throws Exception {
        // Create a book and save it
        Long bookId = 1L;
        Books book = createBook(bookId);

        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/librarian/books/{id}", bookId))
                .andExpect(status().isNoContent());

    }


    // Update convertToObject to handle individual objects
    private static String convertToObjects(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }



    // Using Jackson for object-JSON conversions
    private static String convertToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    private static <T> T convertToObject(String json, TypeReference<List<Books>> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return (T) mapper.readValue(json, clazz);
    }



    // Helper methods for test data creation
    private Member createMember(Long id) {
        Member member = new Member();
        member.setId(id);
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


