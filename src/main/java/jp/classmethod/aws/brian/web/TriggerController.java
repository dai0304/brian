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

import jp.classmethod.aws.brian.model.BrianTriggerRequest;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * Controller implementation to operate trigger groups and triggers.
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
	
	
	/**
	 * Get trigger groups.
	 * 
	 * @return trigger groups
	 * @throws SchedulerException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers", method = RequestMethod.GET)
	public Map<String, Object> getTriggerGroups() throws SchedulerException {
		logger.info("getTriggerGroups");
		
		List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
		
		Map<String, Object> map = new HashMap<>();
		map.put("triggerGroups", triggerGroupNames);
		return map;
	}
	
	/**
	 * Get trigger names in the specified group.
	 * 
	 * @param triggerGroupName groupName
	 * @return trigger names
	 * @throws SchedulerException
	 */
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
	
	/**
	 * Get trigger information for the specified trigger.
	 * 
	 * @param triggerGroupName trigger group name
	 * @param triggerName trigger name
	 * @return trigger information
	 * @throws SchedulerException
	 * @throws ResourceNotFoundException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.GET, produces = "application/json")
	public String getTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("getTrigger {}.{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		if (trigger == null) {
			throw new ResourceNotFoundException();
		}
		
		return gson.toJson(trigger);
	}
	
	/**
	 * Delete specified trigger.
	 * 
	 * @param triggerGroupName trigger group name
	 * @param triggerName trigger name
	 * @return wherther the trigger is removed
	 * @throws SchedulerException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.DELETE)
	public Map<String, Object> deleteTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("deleteTrigger {}.{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		boolean removed = scheduler.unscheduleJob(triggerKey);
		
		Map<String, Object> map = new HashMap<>();
		map.put("removed", removed);
		return map;
	}
	
	/**
	 * Create or update the trigger.
	 * 
	 * @param triggerGroupName trigger group name
	 * @param triggerName trigger name
	 * @param triggerRequest triggerRequest
	 * @return {@link HttpStatus#CREATED} if the trigger is created, {@link HttpStatus#OK} if the trigger is updated.
	 *   And nextFireTime property which is represent that the trigger's next fire time.
	 * @throws SchedulerException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> putTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName,
			@RequestBody BrianTriggerRequest triggerRequest)
			throws SchedulerException, ParseException {
		logger.info("putTrigger {}.{}", triggerGroupName, triggerName);
		logger.info("{}", triggerRequest);
		
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			ScheduleBuilder<? extends Trigger> schedule = getSchedule(triggerRequest);
			
			TriggerBuilder<? extends Trigger> tb = newTrigger()
				.forJob(quartzJob)
				.withIdentity(triggerKey)
				.withSchedule(schedule)
				.withPriority(triggerRequest.getPriority())
				.withDescription(triggerRequest.getDescription());
			
			if (triggerRequest.getJobData() != null) {
				logger.debug("job data map = {}", triggerRequest.getJobData());
				tb.usingJobData(new JobDataMap(triggerRequest.getJobData()));
			}
			
			if (triggerRequest.getStartAt() != null) {
				tb.startAt(triggerRequest.getStartAt());
			} else {
				tb.startNow();
			}
			if (triggerRequest.getEndAt() != null) {
				tb.endAt(triggerRequest.getEndAt());
			}
			
			Trigger trigger = tb.build();
			
			Date nextFireTime;
			HttpStatus status;
			if (scheduler.checkExists(triggerKey)) {
				nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
				status = HttpStatus.OK;
				logger.info("rescheduled {}", triggerKey);
			} else {
				nextFireTime = scheduler.scheduleJob(trigger);
				status = HttpStatus.CREATED;
				logger.info("scheduled {}", triggerKey);
			}
			
			Map<String, Object> map = new HashMap<>();
			map.put("nextFireTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(nextFireTime));
			return new ResponseEntity<>(map, status);
		} catch (ParseException e) {
			logger.error("parse cron expression failed", e);
			Map<String, Object> map = new HashMap<>();
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}
	}
	
	private ScheduleBuilder<? extends Trigger> getSchedule(BrianTriggerRequest triggerRequest) throws ParseException {
		switch (triggerRequest.getScheduleType()) {
			case "oneshot":
				return createOneShotSchedule(triggerRequest);
			case "simple":
				return createSimpleSchedule(triggerRequest);
			case "cron":
			default:
				return createCronSchedule(triggerRequest);
		}
	}
	
	private CronScheduleBuilder createCronSchedule(BrianTriggerRequest triggerRequest) throws ParseException {
		CronExpression cronExpression = new CronExpression(triggerRequest.getRest().get("cronEx"));
		
		String timeZoneId = triggerRequest.getRest().get("timeZone");
		if (timeZoneId != null) {
			cronExpression.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		}
		
		CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(cronExpression);
		
		if (triggerRequest.getMisfireInstruction() != null) {
			switch (triggerRequest.getMisfireInstruction()) {
				case "IGNORE":
					return cronSchedule.withMisfireHandlingInstructionIgnoreMisfires();
				case "DO_NOTHING":
					return cronSchedule.withMisfireHandlingInstructionDoNothing();
				case "FIRE_ONCE_NOW":
					return cronSchedule.withMisfireHandlingInstructionFireAndProceed();
			}
		}
		return cronSchedule;
	}
	
	private SimpleScheduleBuilder createSimpleSchedule(BrianTriggerRequest triggerRequest) {
		long interval;
		int repeatCount;
		try {
			interval = Long.valueOf(triggerRequest.getRest().get("interval"));
			try {
				repeatCount = Integer.valueOf(triggerRequest.getRest().get("repeatCount"));
			} catch (NumberFormatException e) {
				repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			}
		} catch (NumberFormatException e) {
			interval = 0;
			repeatCount = 1;
		}
		
		SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInMilliseconds(interval)
			.withRepeatCount(repeatCount);
		
		if (triggerRequest.getMisfireInstruction() != null) {
			switch (triggerRequest.getMisfireInstruction()) {
				case "IGNORE":
					return simpleSchedule.withMisfireHandlingInstructionIgnoreMisfires();
				case "FIRE_NOW":
					return simpleSchedule.withMisfireHandlingInstructionFireNow();
				case "RESCHEDULE_NEXT_WITH_EXISTING_COUNT":
					return simpleSchedule.withMisfireHandlingInstructionNextWithExistingCount();
				case "RESCHEDULE_NEXT_WITH_REMAINING_COUNT":
					return simpleSchedule.withMisfireHandlingInstructionNextWithRemainingCount();
				case "RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT":
					return simpleSchedule.withMisfireHandlingInstructionNowWithExistingCount();
				case "RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT":
					return simpleSchedule.withMisfireHandlingInstructionNowWithRemainingCount();
			}
		}
		return simpleSchedule;
	}
	
	private SimpleScheduleBuilder createOneShotSchedule(BrianTriggerRequest triggerRequest) {
		SimpleScheduleBuilder oneShotSchedule = SimpleScheduleBuilder.simpleSchedule();
		if (triggerRequest.getMisfireInstruction() != null) {
			switch (triggerRequest.getMisfireInstruction()) {
				case "IGNORE":
					return oneShotSchedule.withMisfireHandlingInstructionIgnoreMisfires();
				case "FIRE_NOW":
					return oneShotSchedule.withMisfireHandlingInstructionFireNow();
			}
		}
		return oneShotSchedule;
	}
}
