package jp.classmethod.aws.brian.model;

import org.quartz.TriggerBuilder;

public class ManualBrianTrigger extends BrianTrigger {
	
	public ManualBrianTrigger(String name, String group) {
		super(TriggerBuilder.newTrigger().withIdentity(name, group).build());
	}
}
