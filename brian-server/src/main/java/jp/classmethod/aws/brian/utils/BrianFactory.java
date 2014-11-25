package jp.classmethod.aws.brian.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.classmethod.aws.brian.BrianClient;
import jp.classmethod.aws.brian.model.BrianCronTrigger;
import jp.classmethod.aws.brian.model.BrianMessage;
import jp.classmethod.aws.brian.model.BrianSimpleTrigger;
import jp.classmethod.aws.brian.model.BrianTrigger;
import jp.classmethod.aws.brian.model.MisFireInstruction;

import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public final class BrianFactory {
	
	public static BrianCronTrigger createBrianCronTrigger(CronTrigger trigger) {
		MisFireInstruction misFireInstruction =
				MisFireInstruction.fromNumberAndClass(trigger.getMisfireInstruction(), BrianCronTrigger.class);
		
		return new BrianCronTrigger.BrianCronTriggerBuilder()
			.withTriggerGroupName(trigger.getKey().getGroup())
			.withTriggerName(trigger.getKey().getName())
			.withMisfireInstruction(misFireInstruction)
			.withDescription(trigger.getDescription())
			.withStartAt(trigger.getStartTime())
			.withEndAt(trigger.getEndTime())
			.withJobData(trigger.getJobDataMap())
			.withTimeZone(trigger.getTimeZone())
			.withCronExpression(trigger.getCronExpression())
			.build();
	}
	
	public static BrianMessage createBrianMessage(JobExecutionContext context) {
		Date fireTime = context.getFireTime();
		Date scheduledFireTime = context.getScheduledFireTime();
		Date prevFireTime = context.getPreviousFireTime();
		Date nextFireTime = context.getNextFireTime();
		int refireCount = context.getRefireCount();
		boolean recovering = context.isRecovering();
		BrianTrigger trigger = createBrianTrigger(context.getTrigger());
		String fireInstanceId = context.getFireInstanceId();
		Map<String, Object> jobData = new LinkedHashMap<>(context.getMergedJobDataMap());
		return new BrianMessage(BrianClient.getVersionString(), fireTime, scheduledFireTime, prevFireTime,
				nextFireTime, refireCount, recovering, fireInstanceId, trigger, jobData);
	}
	
	public static BrianSimpleTrigger createBrianSimpleTrigger(SimpleTrigger trigger) {
		MisFireInstruction misFireInstruction =
				MisFireInstruction.fromNumberAndClass(trigger.getMisfireInstruction(), BrianSimpleTrigger.class);
		
		return new BrianSimpleTrigger.BrianSimpleTriggerBuilder()
			.withTriggerGroupName(trigger.getKey().getGroup())
			.withTriggerName(trigger.getKey().getName())
			.withMisfireInstruction(misFireInstruction)
			.withDescription(trigger.getDescription())
			.withStartAt(trigger.getStartTime())
			.withEndAt(trigger.getEndTime())
			.withJobData(trigger.getJobDataMap())
			.withRepeatInterval(trigger.getRepeatInterval())
			.withRepeatCount(trigger.getRepeatCount())
			.build();
	}
	
	/**
	 * Returns {@link BrianTrigger} instance which correnspond to {@link Trigger}.
	 * 
	 * @param trigger
	 * @return {@link BrianTrigger}
	 */
	public static BrianTrigger createBrianTrigger(Trigger trigger) {
		if (trigger instanceof CronTrigger) {
			return createBrianCronTrigger((CronTrigger) trigger);
		} else if (trigger instanceof SimpleTrigger) {
			return createBrianSimpleTrigger((SimpleTrigger) trigger);
		}
		return null;
	}
	
	private BrianFactory() {
	}
}
