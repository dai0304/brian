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
package jp.classmethod.aws.brian.utils;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utiliti class to print all system properties.
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
public class InitializationUtil {
	
	private static Logger logger = LoggerFactory.getLogger(InitializationUtil.class);
	
	
	public static void logAllProperties() {
		logger.info("======== System Properties ========");
		Map<Object, Object> sortedProps = new TreeMap<>(System.getProperties());
		for (Map.Entry<Object, Object> e : sortedProps.entrySet()) {
			logger.info("{} = {}", e.getKey(), e.getValue());
		}
		logger.info("===================================");
	}
}
