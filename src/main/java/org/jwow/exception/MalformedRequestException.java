package org.jwow.exception;

public class MalformedRequestException extends Exception {
	private static final long serialVersionUID = 1L;

	public MalformedRequestException() {
	}

	MalformedRequestException(String msg) {
		super(msg);
	}

	MalformedRequestException(Exception x) {
		super(x);
	}
}