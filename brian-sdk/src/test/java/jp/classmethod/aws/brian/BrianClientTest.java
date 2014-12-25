package jp.classmethod.aws.brian;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import jp.classmethod.aws.brian.model.BrianClientException;
import jp.classmethod.aws.brian.model.BrianCronTrigger;
import jp.classmethod.aws.brian.model.BrianCronTrigger.BrianCronTriggerBuilder;
import jp.classmethod.aws.brian.model.BrianSimpleTrigger;
import jp.classmethod.aws.brian.model.BrianTrigger;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.TriggerKey;

@SuppressWarnings("javadoc")
public class BrianClientTest {
	
	BrianClient sut = new BrianClient("localhost", 8080);
	
	
	@Test
	public void listTriggerGroups_with_no_triggers() throws Exception {
		// Exercise
		List<String> actual = sut.listTriggerGroups();
		// Verify
		assertThat(actual.size(), is(0));
	}
	
	@Test
	public void listTriggerGroups_with_1_trigger() throws Exception {
		// SetUp
		createCronTrigger();
		try {
			// Exercise
			List<String> actual = sut.listTriggerGroups();
			// Verify
			assertThat(actual.size(), is(1));
			assertThat(actual.get(0), is("g1"));
		} finally {
			// TearDown
			deleteCronTrigger();
		}
	}
	
	@Test
	public void listTriggers_with_no_triggers() throws Exception {
		// Exercise
		List<String> actual = sut.listTriggers("g1");
		// Verify
		assertThat(actual.size(), is(0));
	}
	
	@Test
	public void listTriggers_with_1_triggers() throws Exception {
		// SetUp
		createCronTrigger();
		try {
			// Exercise
			List<String> actual = sut.listTriggers("g1");
			// Verify
			assertThat(actual.size(), is(1));
			assertThat(actual.get(0), is("t1"));
		} finally {
			// TearDown
			deleteCronTrigger();
		}
	}
	
	@Test
	public void createTrigger_with_no_triggers() throws Exception {
		// SetUp
		BrianCronTrigger trigger = new BrianCronTriggerBuilder()
			.withTriggerGroupName("g1")
			.withTriggerName("t1")
			.withCronExpression("*/30 * * * * ?")
			.build();
		long es = Instant.now().getEpochSecond();
		Instant next = Instant.ofEpochSecond(es - (es % 30) + 30);
		CreateTriggerResult expected = new CreateTriggerResult(next);
		try {
			// Exercise
			CreateTriggerResult actual = sut.createTrigger(trigger);
			// Verify
			assertThat(actual, is(expected));
		} finally {
			// TearDown
			deleteCronTrigger();
		}
	}
	
	@Test
	public void describeTrigger_with_no_triggers() throws Exception {
		// SetUp
		TriggerKey key = new TriggerKey("g1", "t1");
		// Exercise
		Optional<BrianTrigger> trigger = sut.describeTrigger(key);
		// Verify
		assertThat(trigger.isPresent(), is(false));
	}
	
	@Test
	public void describeTrigger_with_1_cron_triggers() throws Exception {
		try {
			// SetUp
			createCronTrigger();
			TriggerKey key = new TriggerKey("g1", "t1");
			// Exercise
			Optional<BrianTrigger> trigger = sut.describeTrigger(key);
			// Verify
			assertThat(trigger.isPresent(), is(true));
			BrianTrigger brianTrigger = trigger.get();
			assertThat(brianTrigger.getGroup(), is("g1"));
			assertThat(brianTrigger.getName(), is("t1"));
			assertThat(brianTrigger, is(instanceOf(BrianCronTrigger.class)));
			BrianCronTrigger brianCronTrigger = BrianCronTrigger.class.cast(brianTrigger);
			assertThat(brianCronTrigger.getCronExpression(), is("*/30 * * * * ?"));
			assertThat(brianCronTrigger.getTimeZone().get().getID(), is("Universal"));
		} finally {
			// TearDown
			deleteCronTrigger();
		}
	}
	
	@Test
	public void describeTrigger_with_1_simple_triggers() throws Exception {
		try {
			// SetUp
			createSimpleTrigger();
			TriggerKey key = new TriggerKey("g1", "t2");
			// Exercise
			Optional<BrianTrigger> trigger = sut.describeTrigger(key);
			// Verify
			assertThat(trigger.isPresent(), is(true));
			BrianTrigger brianTrigger = trigger.get();
			assertThat(brianTrigger.getGroup(), is("g1"));
			assertThat(brianTrigger.getName(), is("t2"));
			assertThat(brianTrigger, is(instanceOf(BrianSimpleTrigger.class)));
			BrianSimpleTrigger brianSimpleTrigger = BrianSimpleTrigger.class.cast(brianTrigger);
			assertThat(brianSimpleTrigger.getRepeatInterval(), is(300000L));
			assertThat(brianSimpleTrigger.getRepeatCount(), is(-1));
		} finally {
			// TearDown
			deleteSimpleTrigger();
		}
	}
	
	@Test
	public void deleteTrigger_with_no_triggers() throws Exception {
		// SetUp
		TriggerKey key = new TriggerKey("g1", "t1");
		// Exercise
		try {
			sut.deleteTrigger(key);
			fail();
		} catch (BrianClientException e) {
			assertThat(e.getMessage(), is("triggerKey (g1/t1) is not found"));
		}
	}
	
	@Test
	public void deleteTrigger_with_1_triggers() throws Exception {
		// SetUp
		createCronTrigger();
		TriggerKey key = new TriggerKey("g1", "t1");
		try {
			// Exercise
			sut.deleteTrigger(key);
		} catch (Exception e) {
			// TearDown
			deleteCronTrigger();
		}
	}
	
	private void createCronTrigger() throws Exception {
		sut.createTrigger(new BrianCronTriggerBuilder()
			.withTriggerGroupName("g1")
			.withTriggerName("t1")
			.withCronExpression("*/30 * * * * ?")
			//			.withTimeZone(TimeZone.getTimeZone("Universal"))
			.build());
	}
	
	private void createSimpleTrigger() throws Exception {
		sut.createTrigger(new BrianSimpleTrigger.BrianSimpleTriggerBuilder()
			.withTriggerGroupName("g1")
			.withTriggerName("t2")
			.withRepeatInterval(300000)
			.withRepeatCount(-1)
			.build());
	}
	
	private void deleteCronTrigger() throws Exception {
		sut.deleteTrigger(new TriggerKey("g1", "t1"));
	}
	
	private void deleteSimpleTrigger() throws Exception {
		sut.deleteTrigger(new TriggerKey("g1", "t2"));
	}
}
