package jp.classmethod.aws.brian;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import jp.classmethod.aws.brian.model.CreateTriggerRequest;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.ListTriggerGroupsResult;

import org.junit.Test;


public class BrianClientTest {
	
	BrianClient sut = new BrianClient("localhost", 8080);
	
	@Test
	public void listTriggerGroups() throws Exception {
		// Exercise
		ListTriggerGroupsResult actual = sut.listTriggerGroups();
		// Verify
		assertThat(actual.getContent().size(), is(0));
	}
	
	@Test
	public void createTrigger() throws Exception {
		// SetUp
		CreateTriggerRequest req = mock(CreateTriggerRequest.class);
		CreateTriggerResult expected = new CreateTriggerResult();
		// Exercise
		CreateTriggerResult actual = sut.createTrigger(req);
		// Verify
		assertThat(actual, is(expected));
	}
}
