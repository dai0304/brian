/*
 * Copyright 2011 Daisuke Miyamoto. (http://d.hatena.ne.jp/daisuke-m)
 * Created on 2015/03/26
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import jp.classmethod.aws.InstanceMetadata;
import jp.classmethod.aws.brian.job.BrianQuartzJobBean;
import jp.classmethod.aws.brian.job.BrianSchedulerFactoryBean;
import jp.classmethod.aws.brian.utils.GsonFactoryBean;

import com.amazonaws.services.sns.AmazonSNS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * TODO for daisuke
 */
@Configuration
public class QuartzConfiguration {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	InstanceMetadata instanceMetadata;
	
	@Autowired
	AmazonSNS sns;
	
	@Value("#{systemProperties['BRIAN_TOPIC_ARN']}")
	String topicArn;
	
	
	@Bean
	public BrianSchedulerFactoryBean brianSchedulerFactoryBean() {
		BrianSchedulerFactoryBean brianSchedulerFactoryBean = new BrianSchedulerFactoryBean();
		brianSchedulerFactoryBean.setSchedulerName("BrianScheduler");
		brianSchedulerFactoryBean.setDataSource(dataSource);
		brianSchedulerFactoryBean.setTransactionManager(transactionManager);
		brianSchedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
		brianSchedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
		brianSchedulerFactoryBean.setSchedulerContextAsMap(schedulerContextAsMap());
		brianSchedulerFactoryBean.setQuartzProperties(quartzProperties());
		return brianSchedulerFactoryBean;
	}
	
	private Properties quartzProperties() {
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "BrianScheduler");
		properties.setProperty("org.quartz.scheduler.instanceId", instanceMetadata.getInstanceId() + "_");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "86400000"); // 24hr = 86400000ms
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
		properties.setProperty("org.quartz.jobStore.driverDelegateClass",
				"org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		properties.setProperty("org.quartz.jobStore.isClustered", "true");
		properties.setProperty("org.quartz.jobStore.dontSetAutoCommitFalse", "false");
		properties.setProperty("org.quartz.threadPool.class",
				"org.springframework.scheduling.quartz.SimpleThreadPoolTaskExecutor");
		properties.setProperty("org.quartz.threadPool.threadCount", "30");
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		
		properties.setProperty("org.quartz.scheduler.rmi.export", "true");
		properties.setProperty("org.quartz.scheduler.rmi.createRegistry", "true");
		properties.setProperty("org.quartz.scheduler.rmi.registryHost", "localhost");
		properties.setProperty("org.quartz.scheduler.rmi.registryPort", "1099");
		properties.setProperty("org.quartz.scheduler.rmi.serverPort", "1100");
		return properties;
	}
	
	@Bean
	public JobDetailFactoryBean brianQuartzJob() {
		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
		jobDetailFactoryBean.setJobClass(BrianQuartzJobBean.class);
		jobDetailFactoryBean.setGroup("DEFAULT");
		jobDetailFactoryBean.setName("brianQuartzJob");
		jobDetailFactoryBean.setDurability(true);
		return jobDetailFactoryBean;
	}
	
	@Bean
	public GsonFactoryBean gsonFactoryBean() {
		return new GsonFactoryBean();
	}
	
	private Map<String, ?> schedulerContextAsMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("gson", gsonFactoryBean().getObject());
		map.put("sns", sns);
		map.put("topicArn", topicArn);
		return map;
	}
}
