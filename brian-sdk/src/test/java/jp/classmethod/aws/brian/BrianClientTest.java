package jp.classmethod.aws.brian;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.List;

import jp.classmethod.aws.brian.model.BrianCronTrigger;
import jp.classmethod.aws.brian.model.BrianCronTrigger.BrianCronTriggerBuilder;
import jp.classmethod.aws.brian.model.BrianTrigger;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.TriggerKey;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BrianClientTest {
	
	private static Logger logger = LoggerFactory.getLogger(BrianClientTest.class);
	
	BrianClient sut = new BrianClient("localhost", 8080);
	
	@Test
	public void listTriggerGroups() throws Exception {
		// Exercise
		List<String> actual = sut.listTriggerGroups();
		// Verify
		assertThat(actual.size(), is(0));
	}
	
	@Test
	public void listTriggers() throws Exception {
		// Exercise
		List<String> actual = sut.listTriggers("g1");
		// Verify
		assertThat(actual.size(), is(0));
	}
	
	@Test
	public void createTrigger() throws Exception {
		// SetUp
		BrianCronTrigger trigger = new BrianCronTriggerBuilder()
			.withTriggerGroupName("g1")
			.withTriggerName("t1")
			.withCronExpression("*/20 * * * * ?")
			.build();
		long es = Instant.now().getEpochSecond();
		Instant next = Instant.ofEpochSecond(es - (es % 20) + 20);
		CreateTriggerResult expected = new CreateTriggerResult(next);
		// Exercise
		CreateTriggerResult actual = sut.createTrigger(trigger);
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void describeTrigger() throws Exception {
		// SetUp
		TriggerKey key = new TriggerKey("g1", "t1");
		// Exercise
		BrianTrigger trigger = sut.describeTrigger(key);
		// Verify
		logger.info("{}", trigger);
	}
	
	@Test
	public void deleteTrigger() throws Exception {
		// SetUp
		TriggerKey key = new TriggerKey("g1", "t1");
		// Exercise
		sut.deleteTrigger(key);
	}
}
