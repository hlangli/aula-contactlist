package dk.langli.aula.contactlist;

import java.util.Date;

import lombok.Getter;

@Getter
public class Guardian {
	private Long id;
	private Long profileId;
	private String institutionCode;
	private String institutionName;
	private String municipalityCode;
	private String municipalityName;
	private String firstName;
	private String lastName;
	private String gender;
	private String role;
	private String institutionRole;
	private String aulaEmail;
	private Address address;
	private String email;
	private String homePhoneNumber;
	private String mobilePhoneNumber;
	private String workPhoneNumber;
	private String mainGroup;
	private String shortName;
	private String profilePictureUrl;
	private String profilePicture;
	private Boolean newInstitutionProfile;
	private Boolean communicationBlocked;
	private Boolean isPrimary;
	private Date birthday;
	private String institutionProfileDescriptions;
	private Date lastActivity;
	private Boolean hasCustody;
	private Boolean alias;
	private String groups;
	private String relation;
	private Boolean isInternalProfilePicture;
	private String accessLevel;
	private Boolean allowedToViewContactInfo;
	private Boolean consentToShowContactInfo;
	private Boolean deactivated;
	private Boolean currentUserCanSeeProfileDescription;
	private Boolean currentUserCanEditProfileDescription;

}