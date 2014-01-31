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
package net.sf.log4jdbc;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * MySQL用の {@link RdbmsSpecifics} 実装クラス。
 * 
 * <p>log4jdbcにてDATE型のカラムを表示する際に、{@code MM/dd/yyyy}形式で表示されるのがちょっとイラッとして書いた。
 * 通常の拡張ポイントではない為、ハックで組み込んでいる。</p>
 * 
 * @since 1.0
 * @version $Id: MySqlRdbmsSpecifics.java 219 2012-02-10 14:25:50Z daisuke $
 * @author daisuke
 */
public class MySqlRdbmsSpecifics extends RdbmsSpecifics {
	
	private static final String DATE_FORMAT_FOR_MYSQL = "''yyyy-MM-dd HH:mm:ss''/*z*/";
	
	
	/**
	 * インスタンスを生成する。
	 */
	public MySqlRdbmsSpecifics() {
		try {
			Field field = DriverSpy.class.getDeclaredField("rdbmsSpecifics");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, RdbmsSpecifics> rdbmsSpecifics = (Map<String, RdbmsSpecifics>) field.get(null);
			rdbmsSpecifics.put("com.mysql.jdbc.Driver", this);
			rdbmsSpecifics.put("MySQL-AB JDBC Driver", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	String formatParameterObject(Object object) {
		if (object instanceof Date) {
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_FOR_MYSQL, Locale.US);
			format.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("Universal")));
			return format.format(object);
		} else {
			return super.formatParameterObject(object);
		}
	}
}
