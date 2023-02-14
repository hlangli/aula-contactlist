package dk.langli.aula.contactlist;

import java.util.List;

import lombok.Getter;

@Getter
public class Institution {
	private String institutionCode;
	private List<Group> groups;
}
