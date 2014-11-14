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

import jp.classmethod.aws.brian.Version;
import jp.xet.baseunits.timeutil.Clock;

import org.quartz.JobExecutionContext;

public class BrianMessage {
	
	private final String brianVersion = Version.getVersionString();
	
	private final Date fireTime;
	
	private final Date scheduledFireTime;
	
	private final Date prevFireTime;
	
	private final Date nextFireTime;
	
	private final int refireCount;
	
	private final boolean recovering;
	
	private final String fireInstanceId;
	
	private final BrianTrigger trigger;

	private final Map<String,Object> jobData;
	
	
	public BrianMessage(JobExecutionContext context) {
		fireTime = context.getFireTime();
		scheduledFireTime = context.getScheduledFireTime();
		prevFireTime = context.getPreviousFireTime();
		nextFireTime = context.getNextFireTime();
		refireCount = context.getRefireCount();
		recovering = context.isRecovering();
		trigger = BrianTrigger.getInstance(context.getTrigger());
		fireInstanceId = context.getFireInstanceId();
		jobData = new LinkedHashMap<>(context.getMergedJobDataMap());
	}
	
	

	public BrianMessage(Date fireTime, Date scheduledFireTime, Date prevFireTime, Date nextFireTime, int refireCount,
			boolean recovering, String fireInstanceId, BrianTrigger trigger, Map<String, Object> jobData) {
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

	public BrianMessage(Date scheduledFireTime, String fireInstanceId, BrianTrigger trigger, Map<String, Object> jobData) {
		this(Clock.now().asJavaUtilDate(), scheduledFireTime, null, null, 0, false, fireInstanceId, trigger, jobData);
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
