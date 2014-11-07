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

import java.io.IOException;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class TriggerSerializer extends JsonSerializer<Trigger> {
	
	@Override
	public void serialize(Trigger value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
			return;
		}
		
		jgen.writeStartObject();
		if (value.getKey() != null) {
			if (value.getKey().getGroup() != null)
				jgen.writeStringField("group", value.getKey().getGroup());
			if (value.getKey().getName() != null)
				jgen.writeStringField("name", value.getKey().getName());
		}
		if (value.getDescription() != null) {
			jgen.writeStringField("description", value.getDescription());
		}
		if (value.getStartTime() != null) {
			jgen.writeFieldName("startTime");
			provider.defaultSerializeDateValue(value.getStartTime(), jgen);
		}
		if (value.getEndTime() != null) {
			jgen.writeFieldName("endTime");
			provider.defaultSerializeDateValue(value.getEndTime(), jgen);
		}
		if (value.getNextFireTime() != null) {
			jgen.writeFieldName("nextFireTime");
			provider.defaultSerializeDateValue(value.getNextFireTime(), jgen);
		}
		if (value.getJobDataMap() != null) {
			jgen.writeObjectField("jobDataMap", value.getJobDataMap().getWrappedMap());
		}
		jgen.writeNumberField("misfireInstruction", value.getMisfireInstruction());
		jgen.writeNumberField("priority", value.getPriority());
		
		if (value instanceof CronTrigger) {
			CronTrigger cronTrigger = (CronTrigger) value;
			jgen.writeFieldName("cronEx");
			jgen.writeStartObject();
			if (cronTrigger.getCronExpression() != null) {
				jgen.writeStringField("cronExpression", cronTrigger.getCronExpression());
			}
			if (cronTrigger.getTimeZone() != null) {
				provider.defaultSerializeField("timeZone", cronTrigger.getTimeZone(), jgen);
			}
			jgen.writeEndObject();
		}
		if (value instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger) value;
			jgen.writeFieldName("simple");
			jgen.writeStartObject();
			jgen.writeNumberField("repeatInterval", simpleTrigger.getRepeatInterval());
			jgen.writeNumberField("repeatCount", simpleTrigger.getRepeatCount());
			jgen.writeNumberField("timesTriggered", simpleTrigger.getTimesTriggered());
			jgen.writeEndObject();
		}
		
		jgen.writeEndObject();
	}
	
	@Override
	public Class<Trigger> handledType() {
		return Trigger.class;
	}
}
