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
package jp.classmethod.aws;

/**
 * EC2 Instance Metadata.
 * 
 * @since 1.0
 * @author daisuke
 */
public class InstanceMetadata {
	
	private String instanceId;
	
	private String billingProducts;
	
	private String version;
	
	private String imageId;
	
	private String accountId;
	
	private String instanceType;
	
	private String architecture;
	
	private String kernelId;
	
	private String ramdiskId;
	
	private String pendingTime;
	
	private String availabilityZone;
	
	private String devpayProductCodes;
	
	private String privateIp;
	
	private String region;
	
	
	public String getAccountId() {
		return accountId;
	}
	
	public String getArchitecture() {
		return architecture;
	}
	
	public String getAvailabilityZone() {
		return availabilityZone;
	}
	
	public String getBillingProducts() {
		return billingProducts;
	}
	
	public String getDevpayProductCodes() {
		return devpayProductCodes;
	}
	
	public String getImageId() {
		return imageId;
	}
	
	public String getInstanceId() {
		return instanceId;
	}
	
	public String getInstanceType() {
		return instanceType;
	}
	
	public String getKernelId() {
		return kernelId;
	}
	
	public String getPendingTime() {
		return pendingTime;
	}
	
	public String getPrivateIp() {
		return privateIp;
	}
	
	public String getRamdiskId() {
		return ramdiskId;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getVersion() {
		return version;
	}
	
	@Override
	public String toString() {
		return "InstanceMetadata [instanceId=" + instanceId + ", billingProducts=" + billingProducts + ", version="
				+ version + ", imageId=" + imageId + ", accountId=" + accountId + ", instanceType=" + instanceType
				+ ", architecture=" + architecture + ", kernelId=" + kernelId + ", ramdiskId=" + ramdiskId
				+ ", pendingTime=" + pendingTime + ", availabilityZone=" + availabilityZone + ", devpayProductCodes="
				+ devpayProductCodes + ", privateIp=" + privateIp + ", region=" + region + "]";
	}

	
	void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}
