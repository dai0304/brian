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
import java.util.Optional;

/**
 * Trigger descriptor model.
 * 
 * @author daisuke
 * @since 1.0
 */
public abstract class BrianTrigger extends TriggerKey {
	
	String description;
	
	int priority = 5;
	
	Optional<Date> startTime;
	
	Optional<Date> endTime;
	
	Optional<MisFireInstruction> misfireInstruction;
	
	Map<String, Object> jobData;
	
	
	BrianTrigger(String group, String name, Optional<MisFireInstruction> misfireInstruction, String description,
			Optional<Date> startAt, Optional<Date> endAt, Map<String, Object> jobData) {
		super(group, name);
		this.misfireInstruction = misfireInstruction;
		this.description = description;
		startTime = startAt;
		endTime = endAt;
		this.jobData = jobData;
	}
	
	BrianTrigger() {
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public Optional<Date> getStartTime() {
		return startTime;
	}
	
	public Optional<Date> getEndTime() {
		return endTime;
	}
	
	public Optional<MisFireInstruction> getMisfireInstruction() {
		return misfireInstruction;
	}
	
	public Map<String, Object> getJobData() {
		return jobData;
	}
	
	public abstract BrianTriggerRequest toBrianTriggerRequest();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((jobData == null) ? 0 : jobData.hashCode());
		result = prime * result + ((misfireInstruction == null) ? 0 : misfireInstruction.hashCode());
		result = prime * result + priority;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
		BrianTrigger other = (BrianTrigger) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (jobData == null) {
			if (other.jobData != null) {
				return false;
			}
		} else if (!jobData.equals(other.jobData)) {
			return false;
		}
		if (misfireInstruction == null) {
			if (other.misfireInstruction != null) {
				return false;
			}
		} else if (!misfireInstruction.equals(other.misfireInstruction)) {
			return false;
		}
		if (priority != other.priority) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	protected static abstract class BrianTriggerBuilder<T extends BrianTriggerBuilder<T, U>, U> {
		
		protected String group;
		
		protected String name;
		
		protected Optional<MisFireInstruction> misfireInstruction = Optional.empty();
		
		protected String description;
		
		protected Optional<Date> startAt = Optional.empty();
		
		protected Optional<Date> endAt = Optional.empty();
		
		protected Map<String, Object> jobData = new HashMap<>();
		
		
		public abstract U build();
		
		public T withTriggerGroupName(String group) {
			this.group = group;
			return (T) this;
		}
		
		public T withTriggerName(String name) {
			this.name = name;
			return (T) this;
		}
		
		public T withMisfireInstruction(MisFireInstruction misfireInstruction) {
			this.misfireInstruction = Optional.ofNullable(misfireInstruction);
			return (T) this;
		}
		
		public T withDescription(String description) {
			this.description = description;
			return (T) this;
		}
		
		public T withStartAt(Date startAt) {
			this.startAt = Optional.ofNullable(startAt);
			return (T) this;
		}
		
		public T withEndAt(Date endAt) {
			this.endAt = Optional.ofNullable(endAt);
			return (T) this;
		}
		
		public T withJobData(Map<String, Object> jobData) {
			if (jobData == null) {
				throw new IllegalArgumentException("jobData is null"); //$NON-NLS-1$
			}
			this.jobData = jobData;
			return (T) this;
		}
		
		public T withJobData(String key, Object value) {
			jobData.put(key, value);
			return (T) this;
		}
	}
}
