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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller implementation for HTTP Error handling.
 * 
 * @since 1.0
 * @author daisuke
 */
@Controller
@RequestMapping("/error")
@SuppressWarnings("javadoc")
public class ErrorController {
	
	private static Logger logger = LoggerFactory.getLogger(ErrorController.class);
	
	
	/**
	 * Handle "/error".
	 * 
	 * @return
	 * @since 1.0
	 */
	public String index() {
		logger.info("index invoked");
		return "redirect:/";
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@RequestMapping("/404")
	public String notFound(HttpServletRequest req) {
		logger.info("notFound invoked: {}", req);
		return "404 - Not Found";
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@RequestMapping("/403")
	public String forbidden(HttpServletRequest req) {
		logger.info("forbidden invoked {}", req);
		return "403 - Forbidden";
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@RequestMapping("/500")
	public String internalServerError(HttpServletRequest req) {
		logger.info("internalServerError invoked {}", req);
		return "500 - Internal Server Error";
	}
}
