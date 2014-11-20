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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import jp.classmethod.aws.brian.utils.InitializationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianSpringInitializer implements WebApplicationInitializer {
	
	private static Logger logger = LoggerFactory.getLogger(BrianSpringInitializer.class);
	
	private static final Collection<String> REQUIRED_SYSTEM_PROPERTIES = Collections.unmodifiableCollection(
		Arrays.asList(
				"JDBC_CONNECTION_STRING",
				"DB_USERNAME",
				"DB_PASSWORD",
				"BRIAN_TOPIC_ARN"
			));
	
	
	/**
	 * TODO for daisuke
	 * 
	 * @param container {@link ServletContext}
	 * @param createApplicationContext {@code true} to create {@link WebApplicationContext}
	 * @since 1.0
	 */
	public static void doStartup(ServletContext container, boolean createApplicationContext) {
		logger.info("Starting up brian v{}", Version.getVersionString());
		InitializationUtil.logAllProperties();
		InitializationUtil.validateExistRequiredSystemProperties(REQUIRED_SYSTEM_PROPERTIES);
		
		if (createApplicationContext) {
			XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
			rootContext.setConfigLocations("classpath*:applicationContext.xml");
			container.addListener(new ContextLoaderListener(rootContext));
		}
		
		XmlWebApplicationContext dispatcherContext = new XmlWebApplicationContext();
		dispatcherContext.setConfigLocations(new String[] {
			"/WEB-INF/dispatcher-servlet.xml"
		});
		Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}
	
	@Override
	@SuppressWarnings("unused")
	public void onStartup(ServletContext container) throws ServletException {
		doStartup(container, true);
	}
}
