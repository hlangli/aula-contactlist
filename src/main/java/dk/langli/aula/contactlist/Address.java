package dk.langli.aula.contactlist;

import lombok.Getter;

@Getter
public class Address {
	private Long id;
	private String street;
	private Integer postalCode;
	private String postalDistrict;
}