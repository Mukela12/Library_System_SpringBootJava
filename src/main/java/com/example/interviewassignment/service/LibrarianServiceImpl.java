package com.example.interviewassignment.service;

import com.example.interviewassignment.exceptions.BookNotBorrowedException;
import com.example.interviewassignment.exceptions.BookNotFoundException;
import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;
import com.example.interviewassignment.models.Books.BookStatus;
import com.example.interviewassignment.repository.BooksRepository;
import com.example.interviewassignment.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.interviewassignment.exceptions.MemberNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibrarianServiceImpl implements LibrarianService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Override
    public Books getBookById(Long id) {
        Optional<Books> optionalBook = booksRepository.findById(id);
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }
    }


    @Override
    public Books createBook(Books book) {
        return booksRepository.save(book);
    }

    @Override
    public Books updateBook(Long id, Books book) {
        Books existingBook = getBookById(id);

        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setIsbn(book.getIsbn());
            existingBook.setPublicationYear(book.getPublicationYear());
            return booksRepository.save(existingBook);
        } else {
            throw new BookNotFoundException("Book with ID " + id + " not found or unavailable.");
        }
    }

    @Override
    public void deleteBook(Long id) {
        Books book = getBookById(id);
        if (book != null){
            booksRepository.deleteById(id);
        }else{
            throw new BookNotFoundException("Book with ID " + id + " not found therefore can't be deleted.");

        }
    }

    @Override
    public List<Books> getAllBooks() {
        return booksRepository.findAll();
    }



    // Member Management Methods

    @Override
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Member updateMember(Long id, Member member) {
        Member existingMember = getMemberById(id);
        if (existingMember != null) {
            existingMember.setName(member.getName());
            existingMember.setEmail(member.getEmail());
            existingMember.setAddress(member.getAddress());
            existingMember.setPhone(member.getPhone());
            return memberRepository.save(existingMember);
        } else {
            return null;
        }
    }

    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

}

