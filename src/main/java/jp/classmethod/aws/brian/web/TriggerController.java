/*
 * Copyright 2013 Classmethod, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.classmethod.aws.brian.web;

import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * TODO
 * 
 * @since 1.0
 * @author daisuke
 */
@Controller
@SuppressWarnings("javadoc")
public class TriggerController {
	
	private static Logger logger = LoggerFactory.getLogger(TriggerController.class);
	
	@Autowired
	Gson gson;
	
	@Autowired
	Scheduler scheduler;
	
	@Autowired
	@Qualifier("brianQuartzJob")
	JobDetail quartzJob;
	
	
	@ResponseBody
	@RequestMapping(value = "/triggers", method = RequestMethod.GET)
	public Map<String, Object> getTriggerGroups() throws SchedulerException {
		logger.info("getTriggerGroups");
		List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
		
		Map<String, Object> map = new HashMap<>();
		map.put("triggerGroups", triggerGroupNames);
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}", method = RequestMethod.GET)
	public Map<String, Object> getTriggerNames(@PathVariable("triggerGroupName") String triggerGroupName)
			throws SchedulerException {
		logger.info("getTriggerNames {}", triggerGroupName);
		
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));
		
		Iterable<String> names = Iterables.transform(triggerKeys, new Function<TriggerKey, String>() {
			
			@Override
			public String apply(TriggerKey input) {
				return input == null ? null : input.getName();
			}
		});
		
		Map<String, Object> map = new HashMap<>();
		map.put("names", Lists.newArrayList(names));
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.GET, produces = "application/json")
	public String getTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("getTrigger {}:{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		if (trigger == null) {
			throw new ResourceNotFoundException();
		}
		
		return gson.toJson(trigger);
	}
	
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.DELETE)
	public Map<String, Object> deleteTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("deleteTrigger {}:{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		
		boolean removed = scheduler.unscheduleJob(triggerKey);
		
		Map<String, Object> map = new HashMap<>();
		map.put("removed", removed);
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.PUT)
	public Map<String, Object> putTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName,
			@RequestParam(value = "schedule", defaultValue = "cron") String scheduleType,
			@RequestParam MultiValueMap<String, String> rest)
			throws SchedulerException, ParseException {
		logger.info("putTrigger {}:{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		
		ScheduleBuilder<? extends Trigger> schedule = getSchedule(scheduleType, rest);
		
		Trigger trigger = newTrigger()
			.forJob(quartzJob)
			.withIdentity(triggerKey)
			.withSchedule(schedule)
			.build();
		
		Date nextFireTime;
		
		if (scheduler.checkExists(triggerKey)) {
			nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
			logger.info("rescheduled {}", triggerKey);
		} else {
			nextFireTime = scheduler.scheduleJob(trigger);
			logger.info("scheduled {}", triggerKey);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		
		Map<String, Object> map = new HashMap<>();
		map.put("nextFireTime", sdf.format(nextFireTime));
		return map;
	}
	
	private ScheduleBuilder<? extends Trigger> getSchedule(String scheduleType, MultiValueMap<String, String> rest)
			throws ParseException {
		CronExpression cronExpression = new CronExpression(rest.getFirst("cronEx"));
		String timeZoneId = rest.getFirst("timeZone");
		if (timeZoneId != null) {
			cronExpression.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		}
		
		return CronScheduleBuilder.cronSchedule(cronExpression)
			.withMisfireHandlingInstructionIgnoreMisfires();
	}
}
