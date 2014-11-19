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

import java.time.Instant;

public class CreateTriggerResult {
	
	Instant nextFireTime;
	
	
	public CreateTriggerResult(Instant nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	
	public Instant getNextFireTime() {
		return nextFireTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nextFireTime == null) ? 0 : nextFireTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreateTriggerResult other = (CreateTriggerResult) obj;
		if (nextFireTime == null) {
			if (other.nextFireTime != null)
				return false;
		} else if (!nextFireTime.equals(other.nextFireTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CreateTriggerResult [nextFireTime=" + nextFireTime + "]";
	}
}
