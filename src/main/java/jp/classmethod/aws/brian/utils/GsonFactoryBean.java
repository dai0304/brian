package jp.classmethod.aws.brian.utils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.FactoryBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class GsonFactoryBean implements FactoryBean<Gson> {

	@Override
	public Gson getObject() throws Exception {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Class.class, new ClassSerializer());
		builder.registerTypeAdapter(TimeZone.class, new TimeZoneSerializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		Gson gson = builder.create();
		return gson;
	}

	@Override
	public Class<?> getObjectType() {
		return Gson.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}

class ClassSerializer implements JsonSerializer<Class<?>> {

	@Override
	public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getName());
	}
}

class TimeZoneSerializer implements JsonSerializer<TimeZone> {

	@Override
	public JsonElement serialize(TimeZone src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getID());
	}
}

class DateSerializer implements JsonSerializer<Date> {

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		return new JsonPrimitive(sdf.format(src));
	}
}
