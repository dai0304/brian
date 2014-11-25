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

import jp.xet.baseunits.util.TimeZones;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import org.quartz.Trigger;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Custom {@link ObjectMapper} for Brian.
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
public class BrianServerObjectMapperFactoryBean extends AbstractFactoryBean<ObjectMapper> {
	
	private static final Version VERSION = new Version(1, 0, 0, null, "jp.classmethod.aws", "brianServer");
	
	
	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}
	
	@Override
	protected ObjectMapper createInstance() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule brianModule = new SimpleModule("brianServerModule", VERSION);
		brianModule.addSerializer(Trigger.class, new TriggerSerializer());
		mapper.registerModule(brianModule);
		mapper.registerModule(new Jdk8Module());
		
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZones.UNIVERSAL);
		mapper.setDateFormat(df);
		
		return mapper;
	}
}
