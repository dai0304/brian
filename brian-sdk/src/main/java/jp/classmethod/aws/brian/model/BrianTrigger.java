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

public abstract class BrianTrigger extends TriggerKey {
	
	final int priority = 5;
	
	final String description;
	
	final Date startTime;
	
	final Date endTime;
	
	final String misfireInstruction;
	
	final Date finalFireTime;
	
	final Map<String, Object> jobData = new HashMap<>();
	
	
	BrianTrigger(String group, String name, String misfireInstruction, String description, Date startTime, Date endTime,
			Date finalFireTime) {
		super(group,name);
		this.misfireInstruction = misfireInstruction;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.finalFireTime = finalFireTime;
	}
	
	public String getMisfireInstruction() {
		return misfireInstruction;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public Date getFinalFireTime() {
		return finalFireTime;
	}
	
	public abstract BrianTriggerRequest toBrianTriggerRequest();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((finalFireTime == null) ? 0 : finalFireTime.hashCode());
		result = prime * result + ((jobData == null) ? 0 : jobData.hashCode());
		result = prime * result + ((misfireInstruction == null) ? 0 : misfireInstruction.hashCode());
		result = prime * result + priority;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BrianTrigger other = (BrianTrigger) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (finalFireTime == null) {
			if (other.finalFireTime != null)
				return false;
		} else if (!finalFireTime.equals(other.finalFireTime))
			return false;
		if (jobData == null) {
			if (other.jobData != null)
				return false;
		} else if (!jobData.equals(other.jobData))
			return false;
		if (misfireInstruction == null) {
			if (other.misfireInstruction != null)
				return false;
		} else if (!misfireInstruction.equals(other.misfireInstruction))
			return false;
		if (priority != other.priority)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
}
