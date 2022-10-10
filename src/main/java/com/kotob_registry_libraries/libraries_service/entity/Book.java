package com.kotob_registry_libraries.libraries_service.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="book")
@Data
@ToString
public class Book {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="book_id")
	private Long bookId ;

	@Column(name="book_name")
	private String bookName ;

	@Column(name="book_imagelink")
	private String bookImagelink ;

	@Column(name="book_author")
	private String bookAuthor ;

	@Column(name="book_language")
	private String bookLanguage ;

	@Column(name="book_is_available_scanned")
	private Boolean bookIs_available_scanned ;

	@ManyToOne
	private Library library;


}