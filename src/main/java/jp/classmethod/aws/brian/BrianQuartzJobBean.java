package jp.classmethod.aws.brian;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.gson.Gson;


public class BrianQuartzJobBean extends QuartzJobBean {
	
	private static Logger logger = LoggerFactory.getLogger(BrianQuartzJobBean.class);
	
	/** detailed date format */
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS zzz";
	
	
	/**
	 * ログ用の {@link SimpleDateFormat} を新規に生成する。
	 * 
	 * @return {@link SimpleDateFormat}
	 * @since 1.0.0
	 */
	public static SimpleDateFormat createFormat() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Japan")); // TODO localize
		calendar.setMinimalDaysInFirstWeek(4);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTimeInMillis(0);
		format.setCalendar(calendar);
		return format;
	}
	
	/**
	 * Returns a string representation of this map.  The string representation
	 * consists of a list of key-value mappings in the order returned by the
	 * map's <tt>entrySet</tt> view's iterator, enclosed in braces
	 * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
	 * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
	 * the key followed by an equals sign (<tt>"="</tt>) followed by the
	 * associated value.  Keys and values are converted to strings as by
	 * {@link String#valueOf(Object)}.
	 *
	 * @return a string representation of this map
	 */
	private static String toString(JobDataMap jdm) {
		Iterator<Map.Entry<String, Object>> i = jdm.entrySet().iterator();
		if (!i.hasNext()) {
			return "{}";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Map.Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			sb.append(key);
			sb.append('=');
			sb.append(value == jdm ? "(this Map)" : value);
			if (!i.hasNext()) {
				return sb.append('}').toString();
			}
			sb.append(',').append(' ');
		}
	}
	
	private Gson gson;
	
	private AmazonSNS sns;
	
	private String topicArn;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = createFormat();
		
		JobDetail jobDetail = context.getJobDetail();
		
		try {
			logger.info("======== Quartz trigger firing with Spring Batch jobName = {}", jobDetail.getKey().getName());
			logger.info(" scheduledFireTime = {}", sdf.format(context.getScheduledFireTime()));
			logger.info("          fireTime = {}", sdf.format(context.getFireTime()));
			logger.info("      nextFireTime = {}", sdf.format(context.getNextFireTime()));
			logger.info(" previouseFireTime = {}",
					context.getPreviousFireTime() == null ? null : sdf.format(context.getPreviousFireTime()));
			logger.info("       refireCount = {}", context.getRefireCount());
			logger.info("  mergedJobDataMap = {}", toString(context.getMergedJobDataMap()));
			
			logger.info("-------- Quartz jobDetail");
			logger.info("               key = {}", jobDetail.getKey());
			logger.info("              desc = {}", jobDetail.getDescription());
			logger.info("          jobClass = {}", jobDetail.getJobClass());
			logger.info("        jobDataMap = {}", toString(jobDetail.getJobDataMap()));
			
			String json = gson.toJson(context);
			logger.info("-------- Message");
			logger.info(json);
			
			sns.publish(topicArn, json);
			
			logger.info("======== Job Finished");
		} catch (Exception e) {
			logger.error("Failed execute job.", e);
			logger.info("======== Job Failed");
			throw new JobExecutionException(e);
		}
	}
	
	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	public void setSns(AmazonSNS sns) {
		this.sns = sns;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}
}
