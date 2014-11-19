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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller implementation for {@code /} や {@code /healthcheck}.
 * 
 * <p>These endpoints respond simply {@code "ok"}.</p>
 * 
 * @since 1.0
 * @author daisuke
 */
@Controller
@SuppressWarnings("javadoc")
public class HomeController {
	
	@ResponseBody
	@RequestMapping("/")
	public String index() {
		return "ok";
	}
	
	@ResponseBody
	@RequestMapping("/healthcheck")
	public String healthcheck() {
		return "ok";
	}
}
