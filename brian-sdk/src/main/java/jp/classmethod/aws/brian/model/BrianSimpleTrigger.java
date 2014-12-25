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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple trigger model.
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianSimpleTrigger extends BrianTrigger {
	
	private long repeatInterval;
	
	private int repeatCount;
	
	
	/**
	 * 
	 * @param group trigger group name
	 * @param name trigger name
	 * @param misfireInstruction {@link MisFireInstruction}
	 * @param description trigger description
	 * @param startAt the time at which the trigger should occur.
	 * @param endAt the time at which the trigger should quit repeating - regardless of any remaining repeats (based on the trigger's particular repeat settings).
	 * @param jobData
	 * @param repeatInterval the the time interval (in milliseconds) at which the <code>SimpleTrigger</code> should repeat.
	 * @param repeatCount the the number of times the trigger should repeat, after which it will be automatically deleted.
	 */
	public BrianSimpleTrigger(String group, String name, Optional<MisFireInstruction> misfireInstruction,
			String description, Optional<Date> startAt, Optional<Date> endAt, Map<String, Object> jobData,
			long repeatInterval, int repeatCount) {
		super(group, name, misfireInstruction, description, startAt, endAt, jobData);
		this.repeatInterval = repeatInterval;
		this.repeatCount = repeatCount;
	}
	
	BrianSimpleTrigger() {
	}
	
	/**
	 * Returns the the time interval (in milliseconds) at which the <code>SimpleTrigger</code> should repeat.
	 * 
	 * @return the the time interval (in milliseconds)
	 */
	public long getRepeatInterval() {
		return repeatInterval;
	}
	
	/**
	 * Returns the the number of times the trigger should repeat, after which it will be automatically deleted.
	 * 
	 * @return the the number of times the trigger should repeat
	 */
	public int getRepeatCount() {
		return repeatCount;
	}
	
	@Override
	public BrianTriggerRequest toBrianTriggerRequest() {
		BrianTriggerRequest request = new BrianTriggerRequest();
		request.setTriggerName(name);
		request.setScheduleType(ScheduleType.simple);
		request.setPriority(priority);
		request.setDescription(description);
		startTime.ifPresent(v -> request.setStartAt(v));
		endTime.ifPresent(v -> request.setEndAt(v));
		request.setMisfireInstruction(misfireInstruction.orElse(MisFireInstruction.SMART_POLICY).name());
		request.setJobData(jobData);
		
		request.handleUnknown("repeatInterval", repeatInterval);
		request.handleUnknown("repeatCount", repeatCount);
		return request;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + repeatCount;
		result = prime * result + (int) (repeatInterval ^ (repeatInterval >>> 32));
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
		BrianSimpleTrigger other = (BrianSimpleTrigger) obj;
		if (repeatCount != other.repeatCount) {
			return false;
		}
		if (repeatInterval != other.repeatInterval) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BrianSimpleTrigger [")
			.append("getGroup()=").append(getGroup())
			.append(", getName()=").append(getName())
			.append(", getMisfireInstruction()=").append(getMisfireInstruction())
			.append(", getDescription()=").append(getDescription())
			.append(", getStartTime()=").append(getStartTime())
			.append(", getEndTime()=").append(getEndTime())
			.append(", repeatInterval=").append(repeatInterval)
			.append(", repeatCount=").append(repeatCount)
			.append("]");
		return builder.toString();
	}
	
	
	public static class BrianSimpleTriggerBuilder
			extends BrianTriggerBuilder<BrianSimpleTriggerBuilder, BrianSimpleTrigger> {
		
		private long repeatInterval = 1000;
		
		private int repeatCount = 1;
		
		
		@Override
		public BrianSimpleTrigger build() {
			return new BrianSimpleTrigger(group, name, misfireInstruction, description,
					startAt, endAt, new LinkedHashMap<>(jobData),
					repeatInterval, repeatCount);
		}
		
		public BrianSimpleTriggerBuilder withRepeatInterval(long repeatInterval) {
			this.repeatInterval = repeatInterval;
			return this;
		}
		
		public BrianSimpleTriggerBuilder withRepeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
			return this;
		}
	}
}
