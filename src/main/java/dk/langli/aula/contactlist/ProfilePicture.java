package dk.langli.aula.contactlist;

import lombok.Getter;

@Getter
public class ProfilePicture {
	private String bucket;
	private int id;
	private boolean isImageScalingPending;
	private String key;
	private String url;
}
