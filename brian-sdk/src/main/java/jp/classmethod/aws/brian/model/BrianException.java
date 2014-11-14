package jp.classmethod.aws.brian.model;


public abstract class BrianException extends Exception {

	/**
	 * 
	 */
	public BrianException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected BrianException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BrianException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BrianException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BrianException(Throwable cause) {
		super(cause);
	}
}
