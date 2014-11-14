package jp.classmethod.aws.brian.model;


public class BrianServerException extends BrianException {

	public BrianServerException(Throwable cause) {
		super(cause);
	}

	public BrianServerException(int statusCode) {
	}

	public BrianServerException(String message) {
		super(message);
	}
}
