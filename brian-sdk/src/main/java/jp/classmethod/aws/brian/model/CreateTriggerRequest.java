package jp.classmethod.aws.brian.model;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class CreateTriggerRequest extends AbstractBrianRequest {
	
	@JsonIgnore
	private String triggerGroupName;
	
	private String triggerName;

	public String getTriggerGroupName() {
		return triggerGroupName;
	}

	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}
	
	public CreateTriggerRequest withTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
		return this;
	}
}
