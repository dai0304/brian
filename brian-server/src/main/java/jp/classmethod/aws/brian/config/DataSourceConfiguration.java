/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2015/03/05
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.classmethod.aws.brian.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link DataSource}に関するSpring Java Configurationクラス。
 * 
 * @since 1.0
 * @author daisuke
 */
@Configuration
@SuppressWarnings("javadoc")
public class DataSourceConfiguration {
	
	@Value("#{systemProperties['JDBC_CONNECTION_STRING']}")
	String url;
	
	@Value("#{systemProperties['DB_USERNAME']}")
	String username;
	
	@Value("#{systemProperties['DB_PASSWORD']}")
	String password;
	
	
	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(username);
		basicDataSource.setPassword(password);
		basicDataSource.setValidationQuery("SELECT 1");
		basicDataSource.setMaxActive(50);
		basicDataSource.setMaxIdle(10);
		basicDataSource.setMinIdle(5);
		return basicDataSource;
	}
	
	@Bean(initMethod = "migrate")
	public Flyway flyway() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource());
		return flyway;
	}
}
