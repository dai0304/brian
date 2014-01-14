package jp.classmethod.aws.myung;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class MyungSchedulerFactoryBean extends SchedulerFactoryBean {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerFactoryBean.class);
	
	@Autowired
	@Qualifier("myungQuartzJob")
	JobDetail myungQuartzJob;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		
		Scheduler scheduler = getScheduler();
		try {
			scheduler.addJob(myungQuartzJob, true, false);
		} catch (SchedulerException e) {
			logger.error("", e);
		}
	}
	
}
