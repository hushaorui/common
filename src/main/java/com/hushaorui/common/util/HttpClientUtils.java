package com.hushaorui.common.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HttpClient工具类
 */
public class HttpClientUtils {

	private CloseableHttpClient httpClient;

	public HttpClientUtils(String url) {
		this(url, null);
	}
	public HttpClientUtils(String url, Map<String, String> headers) {
		String hostname = url.split("/")[2];
		int port;
		if (url.startsWith("https")) {
			port = 443;
		} else {
			port = 80;
		}
		if (hostname.contains(":")) {
			String[] arr = hostname.split(":");
			hostname = arr[0];
			port = Integer.parseInt(arr[1]);
		}
		httpClient = createHttpClient(2, 2, 2, hostname, port, headers);
	}

	private void config(HttpRequestBase httpRequestBase) {
		// 配置请求的超时设置
		int timeOut = 30 * 1000;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut).setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
		httpRequestBase.setConfig(requestConfig);
	}

	/**
	 * 创建HttpClient对象
	 */
	private CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname, int port, Map<String, String> headers) {
		ConnectionSocketFactory socketFactory = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory connectionSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", socketFactory).register("https", connectionSocketFactory).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加
		cm.setMaxTotal(maxTotal);
		// 将每个路由基础的连接增加
		cm.setDefaultMaxPerRoute(maxPerRoute);
		HttpHost httpHost = new HttpHost(hostname, port);
		// 将目标主机的最大连接数增加
		cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
			if (executionCount >= 5) {// 如果已经重试了5次，就放弃
				return false;
			}
			if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
				return true;
			}
			if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
				return false;
			}
			if (exception instanceof InterruptedIOException) {// 超时
				return false;
			}
			if (exception instanceof UnknownHostException) {// 目标服务器不可达
				return false;
			}
			if (exception instanceof SSLException) {// SSL握手异常
				return false;
			}
			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			if (headers != null) {
				headers.forEach(request::setHeader);
			}
			// 如果请求是幂等的，就再次尝试
			return !(request instanceof HttpEntityEnclosingRequest);
		};

		return HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
	}

	private void setPostParams(HttpPost httpPost, Map<String, ?> params) {
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nameValuePairs.add(new BasicNameValuePair(key, params.get(key).toString()));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置get参数
	 */
	private String setGetParams(Map<String, ?> params) {
		if (params == null || params.isEmpty()) {
			return null;
		}

		List<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nameValuePairs.add(new BasicNameValuePair(key, params.get(key).toString()));
		}

		try {
			return EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * GET请求URL获取内容
	 */
	public String post(String url, Map<String, ?> params) throws Exception {
		return post(url, params, null);
	}
	/**
	 * GET请求URL获取内容
	 */
	public String post(String url, Map<String, ?> params, Map<String, String> headers) throws Exception {
		HttpPost httppost = new HttpPost(url);
		if (headers != null) {
			headers.forEach(httppost::setHeader);
		}
		config(httppost);
		setPostParams(httppost, params);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			return result;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * GET请求URL获取内容
	 */
	public String httpGet(String url, Map<String, ?> params) {
		String paramsStr = setGetParams(params);
		String getUrl = url;
		if (paramsStr != null && !paramsStr.isEmpty()) {
			getUrl += "?" + paramsStr;
		}

		HttpGet httpget = new HttpGet(getUrl);
		config(httpget);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}