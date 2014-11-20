/*
 * Copyright 2013-2014 Classmethod, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.classmethod.aws.brian.web;

/**
 * Response entity that the APIs return.
 * 
 * @param <T> content type
 * @author daisuke
 * @since 1.0
 */
public class BrianResponse<T> {
	
	private final boolean success;
	
	private final String message;
	
	private final T content;
	
	
	/**
	 * @param success
	 * @param message
	 * @since 1.0
	 */
	public BrianResponse(boolean success, String message) {
		this(success, message, null);
	}
	
	/**
	 * @param success
	 * @param message
	 * @param content
	 * @since 1.0
	 */
	public BrianResponse(boolean success, String message, T content) {
		this.success = success;
		this.message = message;
		this.content = content;
	}
	
	public T getContent() {
		return content;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isSuccess() {
		return success;
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
