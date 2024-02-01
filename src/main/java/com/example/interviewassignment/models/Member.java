package com.example.interviewassignment.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberid;

    @Setter
    @Getter
    @Column(unique = true)
    private String username;

    @Setter
    @Getter
    @Column(nullable = false)
    private String password; // consider secure hashing

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String email;

    @Setter
    @Getter
    private String address;

    @Setter
    @Getter
    private String phone;

    @Setter
    @Getter
    @ManyToMany(mappedBy = "borrowedBy")
    private Set<Books> borrowedBooks;



    public Long getId() {
        return memberid;
    }

    public void setId(long id) {
        if (this.memberid != null) {
            throw new IllegalStateException("Cannot set ID once it has been generated.");
        }
        this.memberid = id;
    }

    public Member() {
        this.borrowedBooks = new HashSet<>();
    }


}
