package dk.langli.aula.contactlist;

import lombok.Getter;

@Getter
public abstract class Response<D> {
	private Status status;
	private D data;
	private Integer version;
	private String module;
	private String method;
}
