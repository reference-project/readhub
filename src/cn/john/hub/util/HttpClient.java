package cn.john.hub.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @ClassName: HttpClient
 * 
 * @Description: 请求客户端
 * 
 * @author: John
 * 
 * @date: 2017年5月22日 下午4:38:08
 * 
 * 
 */
public class HttpClient {

	private final static Logger log = LogManager.getLogger("logger");
	private HttpHost proxy;
	private CloseableHttpClient httpClient;

	public HttpClient() {
		httpClient = HttpClients.createDefault();
	}

	public HttpClient(String ipAddr, int port) {
		proxy = new HttpHost(ipAddr, port);
		init();
	}

	private void init() {
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
	}

	public String getData(String url) {
		
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
				} else {
					throw new ClientProtocolException("Unexcepted response status:" + status);
				}
			}
		};
		
		String ua = cn.john.hub.util.Consts.USER_AGENT;
		HttpGet httpResq = new HttpGet(url);
		httpResq.setHeader("User-Agent", ua);

		try {
			return httpClient.execute(httpResq, responseHandler);
		} catch (SocketTimeoutException e) {
			log.error("Socket time out!" + e);
			return null;
		} catch (ConnectTimeoutException e) {
			log.error("Connection time out!");
			return null;
		} catch (UnknownHostException e) {
			log.error("You may check the network connection!" + e);
			return null;
		} catch (ClientProtocolException e) {
			log.error(e);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
