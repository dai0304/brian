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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import jp.classmethod.aws.brian.utils.InitializationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SuppressWarnings("javadoc")
public class BrianSpringInitializer implements WebApplicationInitializer {
	
	private static Logger logger = LoggerFactory.getLogger(BrianSpringInitializer.class);
	
	@Override
	public void onStartup(ServletContext container) throws ServletException {
		logger.info("Starting up brian v{}", Version.getVersionString());
		
		XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
		rootContext.setConfigLocations(new String[] {
			"classpath*:applicationContext.xml"
		});
		container.addListener(new ContextLoaderListener(rootContext));
		
		XmlWebApplicationContext dispatcherContext = new XmlWebApplicationContext();
		dispatcherContext.setConfigLocations(new String[] {
			"/WEB-INF/dispatcher-servlet.xml"
		});
		Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		
		InitializationUtil.logAllProperties();
		InitializationUtil.validateExistRequiredSystemProperties(BrianApplication.REQUIRED_SYSTEM_PROPERTIES);
	}
}
