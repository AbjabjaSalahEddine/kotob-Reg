package com.kotob_registry_libraries.libraries_service.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="book")
@Data
@ToString
public class Book {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="book_id")
	private Long id ;

	@Column(name="book_name")
	private String name ;

	@Column(name="book_imagelink")
	private String imageLink ;

	@Column(name="book_availablity")
	private Integer availableQuantity ;

	@Column(name="book_price")
	private Float price ;

	@Column(name="book_author")
	private String author ;

	@Column(name="book_language")
	private String language ;

	@Column(name="book_is_available_scanned")
	private Boolean isSearchable ;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "library_id")
	private Library library;


}
