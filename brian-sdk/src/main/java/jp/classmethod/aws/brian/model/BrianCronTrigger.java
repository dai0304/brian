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
import java.util.TimeZone;

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianCronTrigger extends BrianTrigger {
	
	final TimeZone timeZone;
	
	final String cronExpression;
	
	
	public BrianCronTrigger(String group, String name, String misfireInstruction, String description, Date startTime,
			Date endTime, Date finalFireTime, TimeZone timeZone, String cronExpression) {
		super(group, name, misfireInstruction, description, startTime, endTime, finalFireTime);
		this.timeZone = timeZone;
		this.cronExpression = cronExpression;
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}
	
	@Override
	public BrianTriggerRequest toBrianTriggerRequest() {
		BrianTriggerRequest request = new BrianTriggerRequest();
		request.setTriggerName(name);
		request.setScheduleType(ScheduleType.cron);
		request.setPriority(priority);
		request.setDescription(description);
		request.setStartAt(startTime);
		request.setEndAt(endTime);
		request.setMisfireInstruction(misfireInstruction);
		request.setJobData(jobData);
		
		request.handleUnknown("cronEx", cronExpression);
		request.handleUnknown("timeZone", timeZone == null ? null : timeZone.getID());
		return request;
	}
	
	
	public static class BrianCronTriggerBuilder {
		
		private String group;
		
		private String name;
		
		private String misfireInstruction;
		
		private String description;
		
		private TimeZone timeZone;
		
		private String cronExpression;
		
		
		public BrianCronTrigger build() {
			return new BrianCronTrigger(group, name, misfireInstruction, description, null, null, null, timeZone,
					cronExpression);
		}
		
		public BrianCronTriggerBuilder withTriggerGroupName(String group) {
			this.group = group;
			return this;
		}
		
		public BrianCronTriggerBuilder withTriggerName(String name) {
			this.name = name;
			return this;
		}
		
		public BrianCronTriggerBuilder withMisfireInstruction(String misfireInstruction) {
			this.misfireInstruction = misfireInstruction;
			return this;
		}
		
		public BrianCronTriggerBuilder withDescription(String description) {
			this.description = description;
			return this;
		}
		
		public BrianCronTriggerBuilder withTimeZone(TimeZone timeZone) {
			this.timeZone = timeZone;
			return this;
		}
		
		public BrianCronTriggerBuilder withCronExpression(String cronExpression) {
			this.cronExpression = cronExpression;
			return this;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cronExpression == null) ? 0 : cronExpression.hashCode());
		result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BrianCronTrigger other = (BrianCronTrigger) obj;
		if (cronExpression == null) {
			if (other.cronExpression != null) {
				return false;
			}
		} else if (!cronExpression.equals(other.cronExpression)) {
			return false;
		}
		if (timeZone == null) {
			if (other.timeZone != null) {
				return false;
			}
		} else if (!timeZone.equals(other.timeZone)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BrianCronTrigger [")
			.append("getGroup()=").append(getGroup())
			.append(", getName()=").append(getName())
			.append(", getMisfireInstruction()=").append(getMisfireInstruction())
			.append(", getDescription()=").append(getDescription())
			.append(", getStartTime()=").append(getStartTime())
			.append(", getEndTime()=").append(getEndTime())
			.append(", getFinalFireTime()=").append(getFinalFireTime())
			.append(", timeZone=").append(timeZone)
			.append(", cronExpression=").append(cronExpression)
			.append("]");
		return builder.toString();
	}
}
