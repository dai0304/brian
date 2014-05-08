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

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public abstract class BrianTrigger {
	
	/**
	 * Returns {@link BrianTrigger} instance which correnspond to {@link Trigger}.
	 * 
	 * @param trigger
	 * @return {@link BrianTrigger}
	 */
	public static BrianTrigger getInstance(Trigger trigger) {
		if (trigger instanceof CronTrigger) {
			return new BrianCronTrigger((CronTrigger) trigger);
		} else if (trigger instanceof SimpleTrigger) {
			return new BrianSimpleTrigger((SimpleTrigger) trigger);
		}
		return null;
	}
	
	
	private final String group;
	
	private final String name;
	
	private final int misfireInstruction;
	
	private final String description;
	
	private final Date startTime;
	
	private final Date endTime;
	
	private final Date finalFireTime;

	
	BrianTrigger(Trigger trigger) {
		group = trigger.getKey().getGroup();
		name = trigger.getKey().getName();
		description = trigger.getDescription();
		misfireInstruction = trigger.getMisfireInstruction();
		startTime = trigger.getStartTime();
		endTime = trigger.getEndTime();
		finalFireTime = trigger.getFinalFireTime();
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMisfireInstruction() {
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
}