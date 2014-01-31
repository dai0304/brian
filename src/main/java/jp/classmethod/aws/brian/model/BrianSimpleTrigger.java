package jp.classmethod.aws.brian.model;

import org.quartz.SimpleTrigger;

class BrianSimpleTrigger extends BrianTrigger {
	
	private long repeatInterval;
	
	private int repeatCount;
	
	
	BrianSimpleTrigger(SimpleTrigger trigger) {
		super(trigger);
		repeatInterval = trigger.getRepeatInterval();
		repeatCount = trigger.getRepeatCount();
	}
	
	public long getRepeatInterval() {
		return repeatInterval;
	}
	
	public int getRepeatCount() {
		return repeatCount;
	}
}