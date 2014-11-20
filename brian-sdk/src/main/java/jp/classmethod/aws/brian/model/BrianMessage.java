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

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianMessage {
	
	private final String brianVersion;
	
	private final Date fireTime;
	
	private final Date scheduledFireTime;
	
	private final Date prevFireTime;
	
	private final Date nextFireTime;
	
	private final int refireCount;
	
	private final boolean recovering;
	
	private final String fireInstanceId;
	
	private final BrianTrigger trigger;
	
	private final Map<String, Object> jobData;
	
	
	public BrianMessage(String brianVersion, Date fireTime, Date scheduledFireTime, Date prevFireTime,
			Date nextFireTime, int refireCount, boolean recovering, String fireInstanceId, BrianTrigger trigger,
			Map<String, Object> jobData) {
		this.brianVersion = brianVersion;
		this.fireTime = fireTime;
		this.scheduledFireTime = scheduledFireTime;
		this.prevFireTime = prevFireTime;
		this.nextFireTime = nextFireTime;
		this.refireCount = refireCount;
		this.recovering = recovering;
		this.fireInstanceId = fireInstanceId;
		this.trigger = trigger;
		this.jobData = jobData;
	}
	
	public String getBrianVersion() {
		return brianVersion;
	}
	
	public Date getFireTime() {
		return fireTime;
	}
	
	public Date getScheduledFireTime() {
		return scheduledFireTime;
	}
	
	public Date getPrevFireTime() {
		return prevFireTime;
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
	public int getRefireCount() {
		return refireCount;
	}
	
	public boolean isRecovering() {
		return recovering;
	}
	
	public String getFireInstanceId() {
		return fireInstanceId;
	}
	
	public BrianTrigger getTrigger() {
		return trigger;
	}
	
	public Map<String, Object> getJobData() {
		return jobData;
	}
}
