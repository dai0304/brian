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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import jp.classmethod.aws.brian.model.BrianMessage;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.gson.Gson;

public class BrianQuartzJobBean extends QuartzJobBean {
	
	private static Logger logger = LoggerFactory.getLogger(BrianQuartzJobBean.class);
	
	/** detailed date format */
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS zzz";
	
	
	static SimpleDateFormat createFormat() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Japan")); // TODO localize
		calendar.setMinimalDaysInFirstWeek(4);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTimeInMillis(0);
		format.setCalendar(calendar);
		return format;
	}
	
	static String toString(Date date) {
		if (date == null) {
			return null;
		}
		return createFormat().format(date);
	}

	/**
	 * Returns a string representation of this map.  The string representation
	 * consists of a list of key-value mappings in the order returned by the
	 * map's <tt>entrySet</tt> view's iterator, enclosed in braces
	 * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
	 * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
	 * the key followed by an equals sign (<tt>"="</tt>) followed by the
	 * associated value.  Keys and values are converted to strings as by
	 * {@link String#valueOf(Object)}.
	 *
	 * @return a string representation of this map
	 */
	static String toString(JobDataMap jdm) {
		Iterator<Map.Entry<String, Object>> i = jdm.entrySet().iterator();
		if (!i.hasNext()) {
			return "{}";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Map.Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			sb.append(key);
			sb.append('=');
			sb.append(value == jdm ? "(this Map)" : value);
			if (!i.hasNext()) {
				return sb.append('}').toString();
			}
			sb.append(',').append(' ');
		}
	}
	
	private Gson gson;
	
	private AmazonSNS sns;
	
	private String topicArn;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDetail jobDetail = context.getJobDetail();
			Trigger trigger = context.getTrigger();
			BrianMessage message = new BrianMessage(context);
			String json = gson.toJson(message);
			
			logger.info(">>> ======== Quartz job executed by firing {} with job = {}", trigger.getKey(), jobDetail.getKey());
			logger.info(" scheduledFireTime = {}", toString(context.getScheduledFireTime()));
			logger.info("          fireTime = {}", toString(context.getFireTime()));
			logger.info("      nextFireTime = {}", toString(context.getNextFireTime()));
			logger.info(" previouseFireTime = {}", toString(context.getPreviousFireTime()));
			logger.info("       refireCount = {}", context.getRefireCount());
			logger.info("    fireInstanceId = {}", context.getFireInstanceId());
			logger.info("  mergedJobDataMap = {}", toString(context.getMergedJobDataMap()));
			
			logger.info("-------- Quartz trigger");
			logger.info("               key = {}", trigger.getKey());
			logger.info("              desc = {}", trigger.getDescription());
			logger.info("         startTime = {}", trigger.getStartTime());
			logger.info("           endTime = {}", trigger.getEndTime());
			logger.info("     finalFireTime = {}", trigger.getFinalFireTime());
			logger.info("misfireInstruction = {}", trigger.getMisfireInstruction());
			logger.info("          priority = {}", trigger.getPriority());
			logger.info("        jobDataMap = {}", toString(trigger.getJobDataMap()));
			
			logger.info("-------- Quartz jobDetail");
			logger.info("               key = {}", jobDetail.getKey());
			logger.info("              desc = {}", jobDetail.getDescription());
			logger.info("          jobClass = {}", jobDetail.getJobClass());
			logger.info("        jobDataMap = {}", toString(jobDetail.getJobDataMap()));
			
			logger.info("-------- Brian Message");
			logger.info(json);
			
			sns.publish(topicArn, json);
			
			logger.info("<<< ======== Job Finished");
		} catch (Exception e) {
			logger.error("Failed execute job.", e);
			logger.info("<<< ======== Job Failed");
			throw new JobExecutionException(e);
		}
	}
	
	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	public void setSns(AmazonSNS sns) {
		this.sns = sns;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}
}
