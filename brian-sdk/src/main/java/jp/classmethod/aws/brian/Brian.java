package jp.classmethod.aws.brian;

import jp.classmethod.aws.brian.model.BrianException;
import jp.classmethod.aws.brian.model.CreateTriggerRequest;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.ListTriggerGroupsResult;


public interface Brian {
	
	ListTriggerGroupsResult listTriggerGroups() throws BrianException;

	CreateTriggerResult createTrigger(CreateTriggerRequest req) throws BrianException;
}
