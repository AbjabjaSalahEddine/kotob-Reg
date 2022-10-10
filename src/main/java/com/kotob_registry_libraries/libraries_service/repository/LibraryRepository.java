package com.kotob_registry_libraries.libraries_service.repository;

import com.kotob_registry_libraries.libraries_service.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library,Long> {

}
