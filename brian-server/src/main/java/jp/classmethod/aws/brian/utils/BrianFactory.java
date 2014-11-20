package jp.classmethod.aws.brian.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import jp.classmethod.aws.brian.BrianClient;
import jp.classmethod.aws.brian.model.BrianCronTrigger;
import jp.classmethod.aws.brian.model.BrianMessage;
import jp.classmethod.aws.brian.model.BrianSimpleTrigger;
import jp.classmethod.aws.brian.model.BrianTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public final class BrianFactory {
	
	public static BrianCronTrigger createBrianCronTrigger(CronTrigger trigger) {
		TimeZone timeZone = trigger.getTimeZone();
		String cronExpression = trigger.getCronExpression();
		return new BrianCronTrigger(trigger.getKey().getGroup(), trigger.getKey().getName(),
				toString(trigger.getMisfireInstruction()), trigger.getDescription(), trigger.getStartTime(),
				trigger.getEndTime(), trigger.getFinalFireTime(), timeZone, cronExpression);
	}
	
	public static BrianMessage createBrianMessage(JobExecutionContext context) {
		Date fireTime = context.getFireTime();
		Date scheduledFireTime = context.getScheduledFireTime();
		Date prevFireTime = context.getPreviousFireTime();
		Date nextFireTime = context.getNextFireTime();
		int refireCount = context.getRefireCount();
		boolean recovering = context.isRecovering();
		BrianTrigger trigger = greateBrianTrigger(context.getTrigger());
		String fireInstanceId = context.getFireInstanceId();
		Map<String, Object> jobData = new LinkedHashMap<>(context.getMergedJobDataMap());
		return new BrianMessage(BrianClient.getVersionString(), fireTime, scheduledFireTime, prevFireTime,
				nextFireTime, refireCount, recovering, fireInstanceId, trigger, jobData);
	}
	
	public static BrianSimpleTrigger createBrianSimpleTrigger(SimpleTrigger trigger) {
		long repeatInterval = trigger.getRepeatInterval();
		int repeatCount = trigger.getRepeatCount();
		return new BrianSimpleTrigger(trigger.getKey().getGroup(), trigger.getKey().getName(),
				toString(trigger.getMisfireInstruction()), trigger.getDescription(), trigger.getStartTime(),
				trigger.getEndTime(), trigger.getFinalFireTime(), repeatInterval, repeatCount);
	}
	
	/**
	 * Returns {@link BrianTrigger} instance which correnspond to {@link Trigger}.
	 * 
	 * @param trigger
	 * @return {@link BrianTrigger}
	 */
	public static BrianTrigger greateBrianTrigger(Trigger trigger) {
		if (trigger instanceof CronTrigger) {
			return createBrianCronTrigger((CronTrigger) trigger);
		} else if (trigger instanceof SimpleTrigger) {
			return createBrianSimpleTrigger((SimpleTrigger) trigger);
		}
		return null;
	}
	
	private static String toString(int misfireInstruction) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BrianFactory() {
	}
}
