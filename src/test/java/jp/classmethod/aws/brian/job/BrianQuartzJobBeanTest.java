package jp.classmethod.aws.brian.job;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jp.classmethod.aws.brian.Version;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.gson.GsonBuilder;

@RunWith(MockitoJUnitRunner.class)
public class BrianQuartzJobBeanTest {
	
	@Mock
	Scheduler scheduler;
	
	@Mock
	JobDetail jobDetail;
	
	@Mock
	Trigger trigger;
	
	@Mock
	JobExecutionContext context;
	
	@Mock
	AmazonSNS sns;
	
	@InjectMocks
	BrianQuartzJobBean sut;
	
	
	@Test
	public void testname() throws Exception {
		// Setup
		JobDataMap jobDataMap = new JobDataMap();
		when(context.getScheduler()).thenReturn(scheduler);
		when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(context.getTrigger()).thenReturn(trigger);
		when(trigger.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		String arn = "arn";
		String expectedMessage = "{\"brianVersion\":\""
				+ Version.getVersionString()
				+ "\",\"refireCount\":0,\"recovering\":false,\"jobData\":{}}";
		sut.setGson(new GsonBuilder().create());
		sut.setTopicArn(arn);
		
		// Exercise
		sut.execute(context);
		
		// Verify
		verify(sns).publish(arn, expectedMessage);
	}
}
