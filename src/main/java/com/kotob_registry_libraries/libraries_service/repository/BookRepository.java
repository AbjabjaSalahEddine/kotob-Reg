package com.kotob_registry_libraries.libraries_service.repository;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.*;

@RepositoryRestResource
public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("SELECT b FROM Book b WHERE UPPER(b.name) LIKE %?#{[0].toUpperCase()}%")
    public List<Book> search(String word);

//    @Query("SELECT b,l FROM Book b JOIN Library l")
//    public List<Map> getBookCartData(Long id);

}
