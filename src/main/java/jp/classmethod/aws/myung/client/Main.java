package jp.classmethod.aws.myung.client;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.TriggerKey.*;
import static org.quartz.CronScheduleBuilder.*;

public class Main {
	
	Scheduler scheduler;
	
	
	public static void main(String[] args) throws SchedulerException, ParseException {
		System.setProperty("org.quartz.properties", "quartzclient.properties");
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		Trigger trigger = newTrigger()
			.forJob("myungQuartzJob", "DEFAULT")
			.withIdentity(triggerKey("myTrigger", "myTriggerGroup"))
			.withSchedule(cronSchedule(new CronExpression("0 * * * * ?"))
				.withMisfireHandlingInstructionIgnoreMisfires())
			.build();
		
		scheduler.scheduleJob(trigger);
		
//		scheduler.unscheduleJob(triggerKey("myTrigger", "myTriggerGroup"));
	}
}
