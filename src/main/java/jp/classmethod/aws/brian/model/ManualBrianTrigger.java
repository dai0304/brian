package jp.classmethod.aws.brian.model;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

public class ManualBrianTrigger extends BrianTrigger {
	
	public ManualBrianTrigger(String name, String group) {
		super(new ManualTrigger(name, group));
	}
	
	
	@SuppressWarnings("serial")
	private static class ManualTrigger implements Trigger {
		
		private String name;
		
		private String group;
		
		
		public ManualTrigger(String name, String group) {
			this.name = name;
			this.group = group;
		}
		
		@Override
		public TriggerKey getKey() {
			return new TriggerKey(name, group);
		}
		
		@Override
		public JobKey getJobKey() {
			return null;
		}
		
		@Override
		public String getDescription() {
			return "";
		}
		
		@Override
		public String getCalendarName() {
			return null;
		}
		
		@Override
		public JobDataMap getJobDataMap() {
			return new JobDataMap();
		}
		
		@Override
		public int getPriority() {
			return 5;
		}
		
		@Override
		public boolean mayFireAgain() {
			return false;
		}
		
		@Override
		public Date getStartTime() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Date getEndTime() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Date getNextFireTime() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Date getPreviousFireTime() {
			return null;
		}
		
		@Override
		public Date getFireTimeAfter(Date afterTime) {
			return null;
		}
		
		@Override
		public Date getFinalFireTime() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getMisfireInstruction() {
			return Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
		}
		
		@Override
		public TriggerBuilder<? extends Trigger> getTriggerBuilder() {
			return null;
		}
		
		@Override
		public ScheduleBuilder<? extends Trigger> getScheduleBuilder() {
			return null;
		}
		
		@Override
		public int compareTo(Trigger other) {
			return 0;
		}
		
	}
}
