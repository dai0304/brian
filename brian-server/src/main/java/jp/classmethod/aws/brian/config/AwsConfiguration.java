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

import jp.classmethod.aws.InstanceMetadataFactoryBean;
import jp.classmethod.aws.RegionFactoryBean;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO for daisuke
 */
@Configuration
public class AwsConfiguration {
	
	@Bean
	public InstanceMetadataFactoryBean instanceMetadada() {
		return new InstanceMetadataFactoryBean();
	}
	
	@Bean
	public RegionFactoryBean region() {
		RegionFactoryBean regionFactoryBean = new RegionFactoryBean();
		regionFactoryBean.setDefaultRegion(Regions.AP_NORTHEAST_1);
		return regionFactoryBean;
	}
	
	@Bean
	public AmazonSNS sns() {
		AmazonSNSClient sns = new AmazonSNSClient();
		sns.setRegion(region().getObject());
		return sns;
	}
}
