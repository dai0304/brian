package jp.classmethod.aws.brian.model;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public abstract class BrianTrigger {
	
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

//	private final Map<String,Object> jobDataMap;
	
	
	BrianTrigger(Trigger trigger) {
		group = trigger.getKey().getGroup();
		name = trigger.getKey().getName();
		description = trigger.getDescription();
		misfireInstruction = trigger.getMisfireInstruction();
		startTime = trigger.getStartTime();
		endTime = trigger.getEndTime();
		finalFireTime = trigger.getFinalFireTime();
//		jobDataMap = new HashMap<>(trigger.getJobDataMap());
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
	
//	public Map<String, Object> getJobDataMap() {
//		return jobDataMap;
//	}
}