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

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianSimpleTrigger extends BrianTrigger {
	
	private long repeatInterval;
	
	private int repeatCount;
	
	
	/**
	 * @param group
	 * @param name
	 * @param misfireInstruction
	 * @param description
	 * @param startTime
	 * @param endTime
	 * @param finalFireTime
	 * @param repeatInterval
	 * @param repeatCount
	 */
	public BrianSimpleTrigger(String group, String name, String misfireInstruction, String description, Date startTime,
			Date endTime, Date finalFireTime, long repeatInterval, int repeatCount) {
		super(group, name, misfireInstruction, description, startTime, endTime, finalFireTime);
		this.repeatInterval = repeatInterval;
		this.repeatCount = repeatCount;
	}
	
	public long getRepeatInterval() {
		return repeatInterval;
	}
	
	public int getRepeatCount() {
		return repeatCount;
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
			.append(", getFinalFireTime()=").append(getFinalFireTime())
			.append(", repeatInterval=").append(repeatInterval)
			.append(", repeatCount=").append(repeatCount)
			.append("]");
		return builder.toString();
	}
}
