package com.kotob_registry_libraries.libraries_service.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="library")
@Data
@ToString
public class Library {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="library_id")
	@JsonSerialize
	private Long id ;

	@Column(name="library_name")
	private String name ;

	@Column(name="library_adresse")
	private String address ;

	@Column(name="library_imagelink")
	private String imagelink ;

	@Column(name="library_owner")
	private String libraryowner ;


	@OneToMany(cascade =CascadeType.ALL , mappedBy = "library")
	@JsonBackReference
	private Collection<Book> books ;
}