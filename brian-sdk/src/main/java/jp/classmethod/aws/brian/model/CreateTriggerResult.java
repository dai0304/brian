package jp.classmethod.aws.brian.model;

import org.joda.time.DateTime;

import jp.classmethod.aws.brian.model.CreateTriggerResult.CreateTriggerResultContent;

public class CreateTriggerResult extends AbstractBrianResult<CreateTriggerResultContent> {
	
	public static class CreateTriggerResultContent {
		
		DateTime nextFireTime;
		
		
		public DateTime getNextFireTime() {
			return nextFireTime;
		}
	}
}
