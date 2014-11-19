/*
 * Copyright 2013-2014 Classmethod, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.classmethod.aws.brian;

import java.util.List;

import jp.classmethod.aws.brian.model.BrianException;
import jp.classmethod.aws.brian.model.BrianTrigger;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.TriggerKey;
import jp.classmethod.aws.brian.model.UpdateTriggerResult;

/**
 * Provides an interface for accessing the Brian service.
 * 
 * @author daisuke
 * @since 1.0
 */
public interface Brian {
	
	boolean isAvailable();
	
	List<String> listTriggerGroups() throws BrianException;
	
	List<String> listTriggers(String group) throws BrianException;
	
	CreateTriggerResult createTrigger(BrianTrigger trigger) throws BrianException;
	
	UpdateTriggerResult updateTrigger(BrianTrigger trigger) throws BrianException;
	
	BrianTrigger describeTrigger(TriggerKey key) throws BrianException;
	
	void deleteTrigger(TriggerKey key) throws BrianException;
	
	void forceFireTrigger(TriggerKey key) throws BrianException;
	
}
