package dk.langli.aula.contactlist;

import java.util.List;

import lombok.Getter;

@Getter
public class Profile {
	private String id;
	private InstitutionProfile institutionProfile;
	private List<Institution> institutions;
	private String loginPortalRole;
	private String portalRole;
}
