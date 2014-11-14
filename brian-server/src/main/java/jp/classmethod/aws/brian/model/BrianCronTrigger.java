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

import java.util.TimeZone;

import org.quartz.CronTrigger;

class BrianCronTrigger extends BrianTrigger {
	
	private final TimeZone timeZone;
	
	private final String cronExpression;
	
	
	BrianCronTrigger(CronTrigger trigger) {
		super(trigger);
		timeZone = trigger.getTimeZone();
		cronExpression = trigger.getCronExpression();
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}
}
