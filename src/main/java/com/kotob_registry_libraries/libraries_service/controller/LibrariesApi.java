package com.kotob_registry_libraries.libraries_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kotob_registry_libraries.libraries_service.entity.Library;
import com.kotob_registry_libraries.libraries_service.repository.LibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
public class LibrariesApi {

    @Autowired
    private LibraryRepository libraryRepository;

    @PostMapping("/mylibrary")
    public ResponseEntity<?> addMyLibrary(@RequestBody Library library, Principal principal) {

        libraryRepository.save(library);

        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    @PostMapping("/librariesWithIds")
    public ResponseEntity<?> getLibrariesById(@RequestBody List<Long> ids, Principal principal) throws JsonProcessingException {

        List<Optional<Library>> Result=new ArrayList<>();
//        log.info(Arrays.toString(ids.toArray()));

        ids.stream().forEach(id -> {
            Result.add(libraryRepository.findById(id));
        });


        return new ResponseEntity<List>(Result, HttpStatus.OK);
    }

}
