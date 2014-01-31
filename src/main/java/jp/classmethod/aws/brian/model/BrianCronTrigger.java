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
