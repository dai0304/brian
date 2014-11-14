package jp.classmethod.aws.brian;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.classmethod.aws.brian.model.AbstractBrianResult;
import jp.classmethod.aws.brian.model.BrianClientException;
import jp.classmethod.aws.brian.model.BrianException;
import jp.classmethod.aws.brian.model.BrianServerException;
import jp.classmethod.aws.brian.model.CreateTriggerRequest;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.ListTriggerGroupsResult;

public class BrianClient implements Brian {
	
	private static final String DEFAULT_USER_AGENT = "BrianSdk-" + BrianClient.getVersionString();
	
	
	public static String getVersionString() {
		return "";
	}
	
	public BrianClient() {
	}
	
	public BrianClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	
	private String scheme = "http";
	
	private String hostname = "localhost";
	
	private int port = 80;
	
	private String userAgent = DEFAULT_USER_AGENT;
	
	private int socketTimeout = 3000;
	
	private int connectionTimeout = 3000;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	
	@Override
	public ListTriggerGroupsResult listTriggerGroups() throws BrianException {
		HttpResponse httpResponse = doGetRequest("/triggers");
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		switch (statusCode) {
			case HttpStatus.SC_OK:
				try {
					return mapJson(httpResponse.getEntity(), ListTriggerGroupsResult.class);
				} catch (IOException e) {
					throw new BrianServerException(e);
				}
				
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			default:
				throw new BrianServerException("message"); // TODO メッセージ
		}
	}
	
	public CreateTriggerResult createTrigger(CreateTriggerRequest req) throws BrianException {
		HttpEntity entity;
		try {
			entity = new ByteArrayEntity(mapper.writeValueAsBytes(req));
		} catch (JsonProcessingException e) {
			throw new BrianClientException(e);
		}
		String path = "/triggers/" + req.getTriggerGroupName();
		HttpResponse httpResponse = doPostRequest(path, entity);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		switch (statusCode) {
			case HttpStatus.SC_OK:
				try {
					return mapJson(httpResponse.getEntity(), CreateTriggerResult.class);
				} catch (IOException e) {
					throw new BrianServerException(e);
				}
				
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			default:
				throw new BrianServerException("message"); // TODO メッセージ
		}
	}
	
	HttpResponse doGetRequest(String path) throws BrianException {
		try {
			URI uri = createUri(path);
			HttpUriRequest httpRequest = RequestBuilder.get().setUri(uri).build();
			return doRequest(httpRequest);
		} catch (URISyntaxException e) {
			throw new BrianClientException(e);
		}
	}
	
	HttpResponse doPostRequest(String path, HttpEntity entity) throws BrianException {
		try {
			URI uri = createUri(path);
			HttpUriRequest httpRequest = RequestBuilder.post().setUri(uri).setEntity(entity).build();
			return doRequest(httpRequest);
		} catch (URISyntaxException e) {
			throw new BrianClientException(e);
		}
	}
	
	HttpResponse doRequest(HttpUriRequest httpRequest) throws BrianException {
		try {
			return getClient().execute(httpRequest);
		} catch (ClientProtocolException e) {
			throw new BrianClientException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		}
	}
	
	private <T extends AbstractBrianResult<?>>T mapJson(HttpEntity entity, Class<T> resultClass)
			throws JsonParseException, JsonMappingException, IllegalStateException, IOException {
		return mapper.readValue(entity.getContent(), resultClass);
	}
	
	private URI createUri(String path) throws URISyntaxException {
		return new URI(scheme, null, hostname, port, path, null, null);
	}
	
	private HttpClient getClient() {
		RequestConfig requestConfig =
				RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
		
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Accept-Charset", "utf-8"));
		headers.add(new BasicHeader("User-Agent", userAgent));
		
		HttpClient httpClient =
				HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		return httpClient;
	}
}
