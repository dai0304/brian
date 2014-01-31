package jp.classmethod.aws.brian.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.classmethod.aws.brian.Version;

import org.quartz.JobExecutionContext;

public class BrianMessage {
	
	private final String brianVersion = Version.getVersionString();
	
	private final Date fireTime;
	
	private final Date scheduledFireTime;
	
	private final Date prevFireTime;
	
	private final Date nextFireTime;
	
	private final int refireCount;
	
	private final boolean recovering;
	
	private final String fireInstanceId;
	
	private final BrianTrigger trigger;

	private final Map<String,Object> jobData;
	
	
	public BrianMessage(JobExecutionContext context) {
		fireTime = context.getFireTime();
		scheduledFireTime = context.getScheduledFireTime();
		prevFireTime = context.getPreviousFireTime();
		nextFireTime = context.getNextFireTime();
		refireCount = context.getRefireCount();
		recovering = context.isRecovering();
		trigger = BrianTrigger.getInstance(context.getTrigger());
		fireInstanceId = context.getFireInstanceId();
		jobData = new LinkedHashMap<>(context.getMergedJobDataMap());
	}

	public String getBrianVersion() {
		return brianVersion;
	}
	
	public Date getFireTime() {
		return fireTime;
	}
	
	public Date getScheduledFireTime() {
		return scheduledFireTime;
	}
	
	public Date getPrevFireTime() {
		return prevFireTime;
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
	public int getRefireCount() {
		return refireCount;
	}
	
	public boolean isRecovering() {
		return recovering;
	}
	
	public String getFireInstanceId() {
		return fireInstanceId;
	}
	
	public BrianTrigger getTrigger() {
		return trigger;
	}

	public Map<String, Object> getJobData() {
		return jobData;
	}
}
