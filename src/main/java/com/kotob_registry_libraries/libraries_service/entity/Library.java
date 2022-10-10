package com.kotob_registry_libraries.libraries_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	private Long libraryId ;

	@Column(name="library_name")
	private String libraryName ;

	@Column(name="library_adresse")
	private String libraryAdresse ;

	@Column(name="library_imagelink")
	private String libraryImagelink ;

	@Column(name="library_owner")
	private String libraryOwner ;


	@OneToMany(mappedBy = "library")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Collection<Book> books ;
}