package jp.classmethod.aws.brian.model;


public abstract class AbstractBrianResult<T> {
	
	private boolean success;
	
	private String message;
	
	private T content;

	
	public boolean isSuccess() {
		return success;
	}

	
	public String getMessage() {
		return message;
	}
	
	public T getContent() {
		return content;
	}
}
