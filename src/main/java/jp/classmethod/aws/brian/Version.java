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


/**
 * Portnoy APIのバージョン番号を保持する。
 * 
 * @since 1.0
 * @version $Id: Version.java 6175 2012-06-08 02:20:08Z miyamoto $
 * @author daisuke
 */
public final class Version {
	
	/**
	 * バージョン番号を返す。
	 * 
	 * @return バージョン番号
	 * @since 1.0
	 */
	public static String getVersionString() {
		return "[WORKING]"; // maven-injection-plugin による自動書き換え
	}
	
	private Version() {
	}
}
