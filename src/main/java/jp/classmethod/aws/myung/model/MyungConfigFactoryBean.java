package jp.classmethod.aws.myung.model;

import org.springframework.beans.factory.FactoryBean;


public class MyungConfigFactoryBean implements FactoryBean<MyungConfig> {

	@Override
	public MyungConfig getObject() throws Exception {
		return new MyungConfig();
	}

	@Override
	public Class<?> getObjectType() {
		return MyungConfig.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
