package com.kotob_registry_libraries.libraries_service.repository;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("SELECT b FROM Book b WHERE UPPER(b.bookName) LIKE %?#{[0].toUpperCase()}%")
    public List<Book> search(String word);

}
