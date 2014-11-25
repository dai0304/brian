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
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Cron trigger model.
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianCronTrigger extends BrianTrigger {
	
	private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Universal");
	
	Optional<TimeZone> timeZone;
	
	String cronExpression;
	
	
	/**
	 * Create instance.
	 * 
	 * @param group trigger group name
	 * @param name trigger name
	 * @param misfireInstruction {@link MisFireInstruction}
	 * @param description trigger description
	 * @param startAt the time at which the trigger should occur.
	 * @param endAt the time at which the trigger should quit repeating - regardless of any remaining repeats (based on the trigger's particular repeat settings).
	 * @param jobData
	 * @param timeZone the time zone for which the {@link #cronExpression} of this cron trigger will be resolved.
	 * @param cronExpression the expression of cron
	 */
	public BrianCronTrigger(String group, String name, Optional<MisFireInstruction> misfireInstruction,
			String description, Optional<Date> startAt, Optional<Date> endAt, Map<String, Object> jobData,
			Optional<TimeZone> timeZone, String cronExpression) {
		super(group, name, misfireInstruction, description, startAt, endAt, jobData);
		this.timeZone = timeZone;
		this.cronExpression = cronExpression;
	}
	
	BrianCronTrigger() {
	}
	
	/**
	 * Returns the time zone for which the {@link #cronExpression} of this cron trigger will be resolved.
	 * 
	 * @return the time zone
	 */
	public Optional<TimeZone> getTimeZone() {
		return timeZone;
	}
	
	/**
	 * Returns the expression of cron.
	 * 
	 * @return the expression of cron
	 */
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
		startTime.ifPresent(v -> request.setStartAt(v));
		endTime.ifPresent(v -> request.setEndAt(v));
		request.setMisfireInstruction(misfireInstruction.orElse(MisFireInstruction.SMART_POLICY).name());
		request.setJobData(jobData);
		
		request.handleUnknown("cronEx", cronExpression);
		request.handleUnknown("timeZone", timeZone.orElse(DEFAULT_TIME_ZONE).getID());
		return request;
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
			.append(", timeZone=").append(timeZone)
			.append(", cronExpression=").append(cronExpression)
			.append("]");
		return builder.toString();
	}
	
	
	public static class BrianCronTriggerBuilder
			extends BrianTriggerBuilder<BrianCronTriggerBuilder, BrianCronTrigger> {
		
		private Optional<TimeZone> timeZone = Optional.empty();
		
		private String cronExpression;
		
		
		@Override
		public BrianCronTrigger build() {
			return new BrianCronTrigger(group, name, misfireInstruction, description,
					startAt, endAt, jobData, timeZone, cronExpression);
		}
		
		public BrianCronTriggerBuilder withTimeZone(TimeZone timeZone) {
			this.timeZone = Optional.ofNullable(timeZone);
			return this;
		}
		
		public BrianCronTriggerBuilder withCronExpression(String cronExpression) {
			this.cronExpression = cronExpression;
			return this;
		}
	}
}
