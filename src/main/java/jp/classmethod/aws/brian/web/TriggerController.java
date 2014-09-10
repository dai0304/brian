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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import jp.classmethod.aws.brian.model.BrianResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.services.sns.AmazonSNS;
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
	
	@Autowired
	AmazonSNS sns;
	
	@Value("#{systemProperties['PARAM3'] ?: systemProperties['BRIAN_TOPIC_ARN']}")
	String topicArn;
	
	
	/**
	 * Get trigger groups.
	 * 
	 * @return trigger groups
	 * @throws SchedulerException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers", method = RequestMethod.GET)
	public BrianResponse<List<String>> getTriggerGroups() throws SchedulerException {
		logger.info("getTriggerGroups");
		List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
		Collections.sort(triggerGroupNames);
		logger.info("  result = {}", triggerGroupNames);
		return new BrianResponse<>(triggerGroupNames);
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
	public BrianResponse<List<String>> getTriggerNames(@PathVariable("triggerGroupName") String triggerGroupName)
			throws SchedulerException {
		logger.info("getTriggerNames {}", triggerGroupName);
		
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));
		logger.info("  result = {}", triggerKeys);
		
		List<String> triggerNames = Lists.newArrayList(Iterables.transform(triggerKeys,
				new Function<TriggerKey, String>() {
					
					@Override
					public String apply(TriggerKey input) {
						return input == null ? null : input.getName();
					}
				}));
		Collections.sort(triggerNames);
		return new BrianResponse<List<String>>(triggerNames);
	}
	
	/**
	 * Create trigger.
	 * 
	 * @param triggerGroupName groupName
	 * @return trigger names
	 * @throws SchedulerException
	 */
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}", method = RequestMethod.POST)
	public ResponseEntity<?> createTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@RequestBody BrianTriggerRequest triggerRequest)
			throws SchedulerException {
		logger.info("createTrigger {}.{}", triggerGroupName);
		logger.info("{}", triggerRequest);
		
		String triggerName = triggerRequest.getTriggerName();
		
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			if (scheduler.checkExists(triggerKey)) {
				String message = String.format("trigger %s.%s already exists.", triggerGroupName, triggerName);
				return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.CONFLICT);
			}
			
			Trigger trigger = getTrigger(triggerRequest, triggerKey);
			
			Date nextFireTime = scheduler.scheduleJob(trigger);
			logger.info("scheduled {}", triggerKey);
			
			Map<String, Object> map = new HashMap<>();
			map.put("nextFireTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(nextFireTime));
			return new ResponseEntity<>(new BrianResponse<>(map), HttpStatus.CREATED);
		} catch (ParseException e) {
			logger.warn("parse cron expression failed", e);
			String message = "parse cron expression failed - " + e.getMessage();
			return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Update the trigger.
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
	public ResponseEntity<?> updateTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName,
			@RequestBody BrianTriggerRequest triggerRequest)
			throws SchedulerException {
		logger.info("updateTrigger {}.{}: {}", triggerGroupName, triggerName, triggerRequest);
		
		if (triggerName.equals(triggerRequest.getTriggerName()) == false) {
			String message = String.format("trigger names '%s' in the path and '%s' in the request body is not equal",
					triggerName, triggerRequest.getTriggerName());
			return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.BAD_REQUEST);
		}
		
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			if (scheduler.checkExists(triggerKey) == false) {
				String message = String.format("trigger %s.%s is not found.", triggerGroupName, triggerName);
				return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.NOT_FOUND);
			}
			
			Trigger trigger = getTrigger(triggerRequest, triggerKey);
			
			Date nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
			logger.info("rescheduled {}", triggerKey);
			
			Map<String, Object> map = new HashMap<>();
			map.put("nextFireTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(nextFireTime));
			return new ResponseEntity<>(new BrianResponse<>(map), HttpStatus.OK);
		} catch (ParseException e) {
			logger.warn("parse cron expression failed", e);
			String message = "parse cron expression failed - " + e.getMessage();
			return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.BAD_REQUEST);
		}
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
	public ResponseEntity<?> getTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("getTrigger {}.{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		logger.info("{}", trigger);
		if (trigger == null) {
			throw new ResourceNotFoundException();
		}
		
		return new ResponseEntity<>(trigger, HttpStatus.OK);
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
	public ResponseEntity<?> deleteTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("deleteTrigger {}.{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		if (scheduler.checkExists(triggerKey) == false) {
			String message = String.format("trigger %s.%s is not found.", triggerGroupName, triggerName);
			return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.NOT_FOUND);
		}
		
		boolean deleted = scheduler.unscheduleJob(triggerKey);
		
		if (deleted) {
			return new ResponseEntity<>(new BrianResponse<>(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new BrianResponse<>("unschedule failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/triggers/{triggerGroupName}/{triggerName}", method = RequestMethod.POST)
	public ResponseEntity<?> forceFireTrigger(
			@PathVariable("triggerGroupName") String triggerGroupName,
			@PathVariable("triggerName") String triggerName)
			throws SchedulerException {
		logger.info("forceFireTrigger {}.{}", triggerGroupName, triggerName);
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
		if (scheduler.checkExists(triggerKey) == false) {
			String message = String.format("trigger %s.%s is not found.", triggerGroupName, triggerName);
			return new ResponseEntity<>(new BrianResponse<>(message), HttpStatus.NOT_FOUND);
		}
		
		Trigger trigger = scheduler.getTrigger(triggerKey);
		scheduler.triggerJob(quartzJob.getKey(), trigger.getJobDataMap());
		
		Map<String, Object> map = new HashMap<>();
		return new ResponseEntity<>(new BrianResponse<>(map), HttpStatus.OK);
	}
	
	private Trigger getTrigger(BrianTriggerRequest triggerRequest, TriggerKey triggerKey) throws ParseException {
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
		
		// TODO time validation
		
		Trigger trigger = tb.build();
		return trigger;
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
		long repeatInterval;
		int repeatCount;
		try {
			repeatInterval = Long.valueOf(triggerRequest.getRest().get("repeatInterval"));
			try {
				repeatCount = Integer.valueOf(triggerRequest.getRest().get("repeatCount"));
			} catch (NumberFormatException e) {
				repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			}
		} catch (NumberFormatException e) {
			repeatInterval = 0;
			repeatCount = 1;
		}
		
		SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInMilliseconds(repeatInterval)
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
