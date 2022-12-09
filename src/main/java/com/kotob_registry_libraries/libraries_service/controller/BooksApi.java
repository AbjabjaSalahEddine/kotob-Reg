package com.kotob_registry_libraries.libraries_service.controller;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import com.kotob_registry_libraries.libraries_service.entity.Library;
import com.kotob_registry_libraries.libraries_service.repository.BookRepository;
import com.kotob_registry_libraries.libraries_service.repository.LibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class BooksApi {
    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/booksWithIds")
    public ResponseEntity<?> getBooksByIds(@RequestBody List<Long> ids, Principal principal) {

        List<Optional<Book>> Result=new ArrayList<>();

        ids.stream().forEach(id -> {
            Result.add(bookRepository.findById(id));
        });

        return new ResponseEntity<>(Result, HttpStatus.OK);
    }

    @PostMapping("/mylibrary/{id}/books")
    public ResponseEntity<?> addBook(@RequestBody Book book, Principal principal,@PathVariable Long id) {
        Library l=libraryRepository.findById(id).get();
        book.setLibrary(l);
        bookRepository.save(book);

        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }
}
