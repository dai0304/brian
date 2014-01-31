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
package jp.classmethod.aws.brian.job;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class BrianSchedulerFactoryBean extends SchedulerFactoryBean {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerFactoryBean.class);
	
	@Autowired
	@Qualifier("brianQuartzJob")
	JobDetail quartzJob;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		
		Scheduler scheduler = getScheduler();
		try {
			scheduler.addJob(quartzJob, true, false);
		} catch (SchedulerException e) {
			logger.error("Adding job to the scheduler is failed", e);
		}
	}
}
