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
package jp.classmethod.aws.brian.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Trigger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BrianTriggerRequest {
	
	@JsonProperty
	private String triggerName;
	
	@JsonProperty
	private String scheduleType;
	
	@JsonProperty
	private int priority = Trigger.DEFAULT_PRIORITY;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date startAt;
	
	@JsonProperty
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date endAt;
	
	@JsonProperty
	private String misfireInstruction;
	
	@JsonProperty
	private Map<String, Object> jobData;
	
	@JsonIgnore
	private final Map<String, String> rest = new HashMap<>();
	
	
	// note: name does not matter; never auto-detected, need to annotate
	// (also note that formal argument type #1 must be "String"; second one is usually
	//  "Object", but can be something else -- as long as JSON can be bound to that type)
	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
		rest.put(key, value == null ? null : value.toString());
	}
	
	public String getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getStartAt() {
		return startAt;
	}
	
	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}
	
	public Date getEndAt() {
		return endAt;
	}
	
	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}
	
	public String getMisfireInstruction() {
		return misfireInstruction;
	}
	
	public void setMisfireInstruction(String misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}
	
	public Map<String, Object> getJobData() {
		return jobData;
	}
	
	public void setJobData(Map<String, Object> jobData) {
		this.jobData = jobData;
	}
	
	public Map<String, String> getRest() {
		return rest;
	}
	
	public String getTriggerName() {
		return triggerName;
	}

	
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Override
	public String toString() {
		return "BrianTriggerRequest [triggerName=" + triggerName + ", scheduleType=" + scheduleType + ", priority="
				+ priority + ", description=" + description + ", startAt=" + startAt + ", endAt=" + endAt
				+ ", misfireInstruction=" + misfireInstruction + ", jobData=" + jobData + ", rest=" + rest + "]";
	}
}
