package com.example.interviewassignment.controller;

import com.example.interviewassignment.models.Books;
import com.example.interviewassignment.models.Member;
import com.example.interviewassignment.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/books")
    public ResponseEntity<List<Books>> getAllAvailableBooks() {
        return new ResponseEntity<>(memberService.getAllAvailableBooks(), HttpStatus.OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Books> getBookById(@PathVariable Long id) {
        Books book = memberService.getBookById(id);
        if (book != null) {
            return new ResponseEntity<>(book, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/borrow/{id}")
    public ResponseEntity<Void> borrowBook(@PathVariable Long id, @Valid @RequestBody Member member) {
        memberService.borrowBook(id, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<Void> returnBook(@PathVariable Long id, @RequestBody Member member) {
        memberService.returnBook(id, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @DeleteMapping("/delete/")
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody Member member) {
        memberService.deleteMember(member.getId());
        return ResponseEntity.noContent().build();
    }

}

