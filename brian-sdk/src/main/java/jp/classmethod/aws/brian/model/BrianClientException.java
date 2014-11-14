package jp.classmethod.aws.brian.model;


@SuppressWarnings("serial")
public class BrianClientException extends BrianException {

	public BrianClientException(Exception cause) {
		super(cause);
	}

	public BrianClientException(int statusCode) {
	}
}
