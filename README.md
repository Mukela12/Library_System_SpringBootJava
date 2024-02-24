# Librarian System

## Dependencies
This project relies mainly on Spring Boot. Mainly:
  - Spring Data JPA
  - Spring Security
  - Spring Web
  
Full list of dependencies can be found in [pom.xml][1].

### Scenario
The are two roles in the system; `LIBRARIAN` and `MEMBER`

#### As a Librarian
  - You can add, update, and remove Books from the system
  - You can add, update, view, and remove Member from the system
  
#### As a Member
  - You can view, borrow, and return available Books
  - Once a book is borrowed, its status will change to `BORROWED`
  - Once a book is returned, its status will change to `AVAILABLE`
  - You can delete your own account

## Main focus of this project is to demonstrate my:
  - proper usage of Http Methods and REST practices
  - understanding of SOLID Principle
  - understanding of Design patterns
  - understanding of TDD and BDD

[1]: pom.xml
