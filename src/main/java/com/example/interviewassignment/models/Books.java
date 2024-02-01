package com.example.interviewassignment.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Entity
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getters and setters for all fields
    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String author;

    @Setter
    @Getter
    private String isbn;

    @Setter
    @Getter
    private Integer publicationYear;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(
            name = "book_member",
            joinColumns = @JoinColumn(name = "book_id" , referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "memberid")
    )
    private Set<Member> borrowedBy;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot set ID once it has been generated.");
        }
        this.id = id;
    }



    public enum BookStatus {
        AVAILABLE,
        BORROWED
    }


}