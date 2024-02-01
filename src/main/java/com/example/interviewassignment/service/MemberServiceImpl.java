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
public class MemberServiceImpl implements MemberService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Books getBookById(Long id) {
        return booksRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found."));

        if (member.getBorrowedBooks().isEmpty()){
            memberRepository.delete(member);
        } else {
            member.getBorrowedBooks().forEach(book -> returnBook(book.getId(), member));
            memberRepository.delete(member);
        }
    }

    @Override
    public Member getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found."));
    }

    @Override
    public List<Books> getAllAvailableBooks() {
        return booksRepository.findAvailableBooks();
    }

    @Override
    public void borrowBook(Long bookId, Member member) {
        Books book = booksRepository.findById(bookId).orElse(null);
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            member.getBorrowedBooks().add(book);
            book.setStatus(BookStatus.BORROWED);
            booksRepository.save(book);
            memberRepository.save(member);
        } else {
            throw new BookNotFoundException("Book with ID " + bookId + " not found or unavailable.");
        }
    }

    @Override
    public void returnBook(Long bookId, Member member) {

        Books book = booksRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found."));

        Books bok = null;
        for (Books borrowedBook : member.getBorrowedBooks()) {
            if (borrowedBook.getId().equals(bookId)) {
                // Update book status and member records
                member.getBorrowedBooks().remove(book);
                book.setStatus(BookStatus.AVAILABLE);
                booksRepository.save(book);
                memberRepository.save(member);
                return;
            }
        }

        // Verify member has borrowed the book before proceeding
        if (!member.getBorrowedBooks().contains(book)) {
            throw new BookNotBorrowedException("Book with ID " + bookId + " is not borrowed by you.");
        }


    }


    @Override
    public Member updateMember(Long id, Member member) {
        Member existingMember = memberRepository.findById(id).orElse(null);
        if (existingMember != null) {
            existingMember.setName(member.getName());
            existingMember.setEmail(member.getEmail());
            existingMember.setAddress(member.getAddress());
            existingMember.setPhone(member.getPhone());
            return memberRepository.save(existingMember);
        } else {
            throw new MemberNotFoundException("Member with ID " + id + " not found.");
        }
    }

}
