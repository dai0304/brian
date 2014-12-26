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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.classmethod.aws.brian.model.BrianClientException;
import jp.classmethod.aws.brian.model.BrianCronTrigger;
import jp.classmethod.aws.brian.model.BrianServerException;
import jp.classmethod.aws.brian.model.BrianSimpleTrigger;
import jp.classmethod.aws.brian.model.BrianTrigger;
import jp.classmethod.aws.brian.model.BrianTriggerRequest;
import jp.classmethod.aws.brian.model.CreateTriggerResult;
import jp.classmethod.aws.brian.model.TriggerKey;
import jp.classmethod.aws.brian.model.UpdateTriggerResult;
import jp.classmethod.aws.brian.util.BrianClientObjectMapper;

/**
 * {@link Brian} implementation.
 * 
 * @author daisuke
 * @since 1.0
 */
public class BrianClient implements Brian {
	
	private static Logger logger = LoggerFactory.getLogger(BrianClient.class);
	
	private static final String DEFAULT_USER_AGENT = "BrianSdk-" + BrianClient.getVersionString();
	
	
	/**
	 * Returns version string of Brian client.
	 * 
	 * @return Brian client version
	 * @since 1.0
	 */
	public static String getVersionString() {
		return "0.00-SNAPSHOT";
	}
	
	
	private String scheme = "http";
	
	private String hostname;
	
	private int port;
	
	private String userAgent = DEFAULT_USER_AGENT;
	
	private int socketTimeout = 3000;
	
	private int connectionTimeout = 3000;
	
	private final ObjectMapper mapper = new BrianClientObjectMapper();
	
	
	/**
	 * Create BrianClient to communicate with {@code localhost:80} Brian server.
	 * 
	 * @since 1.0
	 */
	public BrianClient() {
		this("localhost", 80);
	}
	
	/**
	 * Create BrianClient to communicate with Brian server in the specified host.
	 * 
	 * @param hostname hostname the Brian server working on
	 * @param port port the Brian listening on
	 * @since 1.0
	 */
	public BrianClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	
	private Supplier<HttpClient> httpClientSupplier = () -> createAndCacheHttpClient();
	
	
	private synchronized HttpClient createAndCacheHttpClient() {
		class HttpClientSupplier implements Supplier<HttpClient> {
			
			private final HttpClient httpClient;
			
			
			public HttpClientSupplier() {
				RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(connectionTimeout)
					.setSocketTimeout(socketTimeout)
					.build();
				logger.trace("requestConfig = {}", requestConfig);
				
				List<Header> headers = new ArrayList<>();
				headers.add(new BasicHeader("Content-type", "application/json;charset=UTF-8"));
				headers.add(new BasicHeader("User-Agent", userAgent));
				
				httpClient = HttpClientBuilder.create()
					.setDefaultRequestConfig(requestConfig)
					.setDefaultHeaders(headers)
					.build();
				logger.trace("httpClient created");
			}
			
			@Override
			public HttpClient get() {
				return httpClient;
			}
		}
		if (HttpClientSupplier.class.isInstance(httpClientSupplier) == false) {
			httpClientSupplier = new HttpClientSupplier();
		}
		return httpClientSupplier.get();
	}
	
	@Override
	public boolean isAvailable() {
		logger.debug("is available?");
		try {
			URI uri = new URI(scheme, null, hostname, port, "/", null, null);
			HttpUriRequest httpRequest = RequestBuilder.get().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.debug("statusCode: {}", statusCode);
			return statusCode == HttpStatus.SC_OK;
		} catch (Exception e) {
			logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
		}
		return false;
	}
	
	@Override
	public List<String> listTriggerGroups() throws BrianClientException, BrianServerException {
		logger.debug("list trigger groups: {}");
		try {
			URI uri = new URI(scheme, null, hostname, port, "/triggers", null, null);
			HttpUriRequest httpRequest = RequestBuilder.get().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.debug("statusCode: {}", statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				JsonNode tree = mapper.readTree(httpResponse.getEntity().getContent());
				return StreamSupport.stream(tree.spliterator(), false)
					.map(item -> item.textValue())
					.collect(Collectors.toList());
			} else if (statusCode >= 500) {
				throw new BrianServerException("status = " + statusCode);
			} else if (statusCode >= 400) {
				throw new BrianClientException("status = " + statusCode);
			} else {
				throw new Error("status = " + statusCode);
			}
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public List<String> listTriggers(String group) throws BrianClientException, BrianServerException {
		logger.debug("list triggers: {}", group);
		try {
			String path = String.format("/triggers/%s", group);
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.get().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.debug("statusCode: {}", statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				JsonNode tree = mapper.readTree(httpResponse.getEntity().getContent());
				return StreamSupport.stream(tree.spliterator(), false)
					.map(item -> item.textValue())
					.collect(Collectors.toList());
			} else if (statusCode >= 500) {
				throw new BrianServerException("status = " + statusCode);
			} else if (statusCode >= 400) {
				throw new BrianClientException("status = " + statusCode);
			} else {
				throw new Error("status = " + statusCode);
			}
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public CreateTriggerResult createTrigger(BrianTrigger trigger) throws BrianClientException, BrianServerException {
		logger.debug("create trigger: {}/{}", trigger.getGroup(), trigger.getName());
		try {
			BrianTriggerRequest request = trigger.toBrianTriggerRequest();
			String requestBody = mapper.writeValueAsString(request);
			logger.trace("create: requestBody = {}", requestBody);
			HttpEntity entity = new StringEntity(requestBody);
			
			String path = String.format("/triggers/%s", trigger.getGroup());
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.post().setUri(uri).setEntity(entity).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.debug("statusCode: {}", statusCode);
			JsonNode tree = mapper.readTree(httpResponse.getEntity().getContent());
			if (statusCode == HttpStatus.SC_CREATED) {
				String nextFireTime = tree.path("content").path("nextFireTime").asText();
				logger.info("trigger created: nextFireTime = {}", nextFireTime);
				return new CreateTriggerResult(Instant.parse(nextFireTime));
			} else if (statusCode >= 500) {
				throw new BrianServerException(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			} else if (statusCode == HttpStatus.SC_CONFLICT) {
				throw new BrianClientException(String.format("triggerKey (%s/%s) is already exist", new Object[] {
					trigger.getGroup(),
					trigger.getName()
				}));
			} else if (statusCode >= 400) {
				throw new BrianClientException(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			} else {
				throw new Error(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			}
		} catch (JsonProcessingException e) {
			throw new BrianServerException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public UpdateTriggerResult updateTrigger(BrianTrigger trigger) throws BrianClientException, BrianServerException {
		logger.debug("update trigger: {}/{}", trigger.getGroup(), trigger.getName());
		try {
			BrianTriggerRequest request = trigger.toBrianTriggerRequest();
			String requestBody = mapper.writeValueAsString(request);
			logger.trace("update: requestBody = {}", requestBody);
			HttpEntity entity = new StringEntity(requestBody);
			
			String path = String.format("/triggers/%s/%s", trigger.getGroup(), trigger.getName());
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.put().setUri(uri).setEntity(entity).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.debug("statusCode: {}", statusCode);
			JsonNode tree = mapper.readTree(httpResponse.getEntity().getContent());
			if (statusCode == HttpStatus.SC_OK) {
				String nextFireTime = tree.path("content").path("nextFireTime").asText();
				logger.info("trigger updated: nextFireTime = {}", nextFireTime);
				return new UpdateTriggerResult(Instant.parse(nextFireTime));
			} else if (statusCode >= 500) {
				throw new BrianServerException(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BrianClientException(String.format("triggerKey (%s/%s) is not found", new Object[] {
					trigger.getGroup(),
					trigger.getName()
				}));
			} else if (statusCode >= 400) {
				throw new BrianClientException(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			} else {
				throw new Error(String.format("status = %d; message = %s", new Object[] {
					statusCode,
					tree.get("message").textValue()
				}));
			}
		} catch (JsonProcessingException e) {
			throw new BrianServerException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public Optional<BrianTrigger> describeTrigger(TriggerKey key) throws BrianClientException, BrianServerException {
		logger.debug("describe trigger: {}", key);
		try {
			String path = String.format("/triggers/%s/%s", key.getGroup(), key.getName());
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.get().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			JsonNode tree = mapper.readTree(httpResponse.getEntity().getContent());
			if (statusCode == HttpStatus.SC_OK) {
				if (tree.path("cronExpression").isMissingNode() == false) {
					return Optional.of(mapper.readValue(new TreeTraversingParser(tree), BrianCronTrigger.class));
				} else if (tree.path("repeatCount").isMissingNode() == false) {
					return Optional.of(mapper.readValue(new TreeTraversingParser(tree), BrianSimpleTrigger.class));
				}
				// TODO deserialize
				throw new Error("unknown scheduleType");
			} else if (statusCode >= 500) {
				throw new BrianServerException("status = " + statusCode);
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				return Optional.empty();
			} else if (statusCode >= 400) {
				throw new BrianClientException("status = " + statusCode);
			} else {
				throw new Error("status = " + statusCode);
			}
		} catch (JsonProcessingException e) {
			throw new BrianServerException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public void deleteTrigger(TriggerKey key) throws BrianClientException, BrianServerException {
		logger.debug("delete trigger: {}", key);
		try {
			String path = String.format("/triggers/%s/%s", key.getGroup(), key.getName());
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.delete().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				logger.info("trigger deleted: {}", key);
				return;
			} else if (statusCode >= 500) {
				throw new BrianServerException("status = " + statusCode);
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BrianClientException(String.format("triggerKey (%s/%s) is not found", new Object[] {
					key.getGroup(),
					key.getName()
				}));
			} else if (statusCode >= 400) {
				throw new BrianClientException("status = " + statusCode);
			} else {
				throw new Error("status = " + statusCode);
			}
		} catch (JsonProcessingException e) {
			throw new BrianServerException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public void forceFireTrigger(TriggerKey key) throws BrianClientException, BrianServerException {
		logger.debug("force fire: {}", key);
		try {
			String path = String.format("/triggers/%s/%s", key.getGroup(), key.getName());
			URI uri = new URI(scheme, null, hostname, port, path, null, null);
			HttpUriRequest httpRequest = RequestBuilder.post().setUri(uri).build();
			HttpResponse httpResponse = httpClientSupplier.get().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				logger.info("trigger force fired: {}", key);
				return;
			} else if (statusCode >= 500) {
				throw new BrianServerException("status = " + statusCode);
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BrianClientException(String.format("triggerKey (%s/%s) is not found", new Object[] {
					key.getGroup(),
					key.getName()
				}));
			} else if (statusCode >= 400) {
				throw new BrianClientException("status = " + statusCode);
			} else {
				throw new Error("status = " + statusCode);
			}
		} catch (JsonProcessingException e) {
			throw new BrianServerException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new BrianServerException(e);
		} catch (IllegalStateException e) {
			throw new Error(e);
		}
	}
	
	/**
	 * Set the scheme.
	 * 
	 * @param scheme {@code http} or {@code https}
	 * @since 1.0
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	/**
	 * Set the hostname.
	 * 
	 * @param hostname hostname
	 * @since 1.0
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * Set the port.
	 * 
	 * @param port 80 and so on
	 * @since 1.0
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Set the user-agent name.
	 * 
	 * @param userAgent user-agent value
	 * @since 1.0
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	/**
	 * Set the socket timeout.
	 * 
	 * @param socketTimeout socket timeout
	 * @since 1.0
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	/**
	 * Set the connection timeout.
	 * 
	 * @param connectionTimeout connection timeout
	 * @since 1.0
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
}
