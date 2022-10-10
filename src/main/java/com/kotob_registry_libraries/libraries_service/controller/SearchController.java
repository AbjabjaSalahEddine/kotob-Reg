package com.kotob_registry_libraries.libraries_service.controller;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import com.kotob_registry_libraries.libraries_service.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SearchController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {

        System.out.println(query);
        String[] words = query.trim().replaceAll("\\s{2,}", " ").split(" ");

        HashMap<Book,Integer> searchedBooks = new HashMap<>();

        for (String word : words) {
            System.out.println("Search for word : "+word);
            List<Book> r=this.bookRepository.search(word);
            for (Book b : r) {
                System.out.println(b.getBookName());
                if(searchedBooks.containsKey(b)) searchedBooks.put(b,searchedBooks.get(b)+1);
                else searchedBooks.put(b,0);
            }
        }

        for (Book b: searchedBooks.keySet()) {
            String key = b.getBookName();
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
                    System.out.println(entry.getKey().getBookName());
                    Result.add(entry.getKey());
                }
            }
        }
        Result = Result.stream().distinct().collect(Collectors.toList());






        return new ResponseEntity<List<Book>>(Result, HttpStatus.OK);

    }
}
