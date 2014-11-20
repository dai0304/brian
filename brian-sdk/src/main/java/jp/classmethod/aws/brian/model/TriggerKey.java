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
package jp.classmethod.aws.brian.model;

/**
 * The key to uniquely specify the trigger.
 * 
 * @author daisuke
 * @since 1.0
 */
public class TriggerKey {
	
	final String group;
	
	final String name;
	
	
	/**
	 * Create the {@link TriggerKey} with group and trigger name.
	 * 
	 * @param group group name
	 * @param name trigger name
	 * @since 1.0
	 */
	public TriggerKey(String group, String name) {
		this.group = group;
		this.name = name;
	}
	
	/**
	 * Retrun the group name.
	 * 
	 * @return the group name
	 * @since 1.0
	 */
	public String getGroup() {
		return group;
	}
	
	/**
	 * Retrun the trigger name.
	 * 
	 * @return the trigger name
	 * @since 1.0
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TriggerKey other = (TriggerKey) obj;
		if (group == null) {
			if (other.group != null) {
				return false;
			}
		} else if (!group.equals(other.group)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "TriggerKey [group=" + group + ", name=" + name + "]";
	}
}
