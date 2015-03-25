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
package jp.classmethod.aws.brian;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import jp.classmethod.aws.brian.utils.InitializationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 1.0
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("jp.classmethod.aws.brian")
@SpringBootApplication
public class BrianApplication {
	
	private static Logger logger = LoggerFactory.getLogger(BrianApplication.class);
	
	private static final Collection<String> REQUIRED_SYSTEM_PROPERTIES = Collections.unmodifiableCollection(
		Arrays.asList(
				"JDBC_CONNECTION_STRING",
				"DB_USERNAME",
				"DB_PASSWORD",
				"BRIAN_TOPIC_ARN"
			));
	
	
	/**
	 * Main method.
	 * 
	 * @param args ignored
	 * @since 1.0
	 */
	public static void main(String[] args) {
		logger.info("Starting up brian v{}", Version.getVersionString());
		InitializationUtil.logAllProperties();
		InitializationUtil.validateExistRequiredSystemProperties(REQUIRED_SYSTEM_PROPERTIES);
		SpringApplication.run(BrianApplication.class, args);
	}
}
