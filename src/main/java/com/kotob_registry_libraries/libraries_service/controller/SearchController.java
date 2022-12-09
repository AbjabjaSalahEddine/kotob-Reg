package com.kotob_registry_libraries.libraries_service.controller;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import com.kotob_registry_libraries.libraries_service.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SearchController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * This Function Searches in Books for the books with a name looking like the param @query
     * sorted in a way that the books with more similar words with query's ones appear first.
     *
     * @param query
     * @param principal
     * @return
     */
    @GetMapping("/books/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        log.info("QUERY : "+query);
        String[] words = query.trim().replaceAll("\\s{2,}", " ").split(" ");

        HashMap<Book,Integer> searchedBooks = new HashMap<>();

        for (String word : words) {
            System.out.println("Search for word : "+word);
            List<Book> r=this.bookRepository.search(word);
            for (Book b : r) {
                System.out.println(b.getName());
                if(searchedBooks.containsKey(b)) searchedBooks.put(b,searchedBooks.get(b)+1);
                else searchedBooks.put(b,0);
            }
        }

        for (Book b: searchedBooks.keySet()) {
            String key = b.getName();
            Integer value = searchedBooks.get(b);
        }

        List<Integer> list = new ArrayList<>();

        LinkedHashMap<Book, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<Book, Integer> entry : searchedBooks.entrySet()) {
            list.add(entry.getValue());
        }

        list=list.stream().sorted((o1, o2) -> o2.compareTo(o1)).collect(Collectors.toList());

        List<Book> Result=new ArrayList<>();

        for (Integer i : list) {
            System.out.println(i);
            for (Map.Entry<Book, Integer> entry : searchedBooks.entrySet()) {
                if (entry.getValue().equals(i)) {
                    System.out.println(entry.getKey().getName());
                    Result.add(entry.getKey());
                }
            }
        }
        log.info(Result.get(0).getName());

        Result = Result.stream().distinct().collect(Collectors.toList());

        log.info(String.valueOf(Result.get(0).getLibrary().getBooks().size()));
        log.info(String.valueOf(Result.size()));

        return new ResponseEntity<List<Book>>(Result, HttpStatus.OK);

    }



}
