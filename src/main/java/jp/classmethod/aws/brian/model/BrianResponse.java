package jp.classmethod.aws.brian.model;

public class BrianResponse<T> {
	
	private final boolean success;
	
	private final String message;
	
	private final T content;
	
	
	/**
	 * @param success
	 * @param message
	 * @param content
	 */
	public BrianResponse(boolean success, String message, T content) {
		this.success = success;
		this.message = message;
		this.content = content;
	}
	
	/**
	 * @param message
	 */
	public BrianResponse(String message) {
		this(false, message, null);
	}
	
	/**
	 * @param message
	 */
	public BrianResponse(T content) {
		this(true, null, content);
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public T getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		if (success) {
			return "BrianResponse [success, content=" + content + "]";
		} else {
			return "BrianResponse [faled, message=" + message + "]";
		}
	}
}
