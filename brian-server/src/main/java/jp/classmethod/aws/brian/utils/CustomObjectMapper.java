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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Custom {@link ObjectMapper} implementation for Brian.
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("serial")
public class CustomObjectMapper extends ObjectMapper {
	
	private static final Version VERSION = new Version(1, 0, 0, null, "jp.classmethod.aws", "brian");
	private static final TimeZone UTC = TimeZone.getTimeZone("Universal");
	
	
	/**
	 * Instantiate.
	 */
	public CustomObjectMapper() {
		SimpleModule brianModule = new SimpleModule("brianModule", VERSION);
		brianModule.addSerializer(new TriggerSerializer());
		registerModule(brianModule);
		
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(UTC);
		setDateFormat(df);
	}
}
