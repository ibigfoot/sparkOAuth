package sparkTutorial;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIUtils {

	/*
	 * troy@documenter.org - prod callback
	 * troy@documenter.dev - localhost callback
	 */
	private String clientId = "3MVG9HxRZv05HarS85y9sOHV7cqSXjzJryBBT8TOhVvBIRo.EAxl4onR1Bq8DSN5toybqAVST6CkF9z8k_6ov";
	private String redirect = "http://localhost:4567/oauth/_callback";
	private String clientSecret = "6724806878769054841";
	
	private static Logger LOG = LoggerFactory.getLogger(APIUtils.class); 
	
	public static final String AUTH_TOKEN = "authToken";
	public static final String CLIENT_ID = "clientId";
	public static final String CLIENT_SECRET = "clientSecret";
	public static final String REDIRECT_URI = "redirectURI";
	
	public APIUtils () {
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		if (processBuilder.environment().get("CLIENT_ID") != null) {
			clientId = processBuilder.environment().get("CLIENT_ID");
		}
		
		if(processBuilder.environment().get("REDIRECT_URI") != null) {
			redirect = processBuilder.environment().get("REDIRECT_URI"); 
		}
		if(processBuilder.environment().get("CLIENT_SECRET") != null) {
			clientSecret = processBuilder.environment().get("CLIENT_SECRET"); 
		}
	}
	
	public String getClientId() {
		return this.clientId;
	}
	public String getRedirectURI() {
		return this.redirect;
	}
	
	
	public JSONObject getFromUrl(String url, String accessToken, Map<String, String> urlParams) throws ClientProtocolException, IOException{
	
		if(urlParams != null && !urlParams.isEmpty()) {
			url += "?";
			for(String s : urlParams.keySet()) {
				url += s + "=" + URLEncoder.encode(urlParams.get(s), "UTF-8");
			}
		}
		LOG.info("We are calling GET on [{}]"+url);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		
		get.addHeader("Authorization:", "Bearer "+accessToken);
		HttpResponse response = client.execute(get);
		
		return processResponse(response);
	}
	
	public JSONObject postToUrl(String url, String accessToken, Map<String, String> postParams) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		post.addHeader("Authorization:", "Bearer "+accessToken);
		
		if(postParams != null && !postParams.isEmpty()) {
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
			
			for(String s : postParams.keySet()) {
				urlParams.add(new BasicNameValuePair(s, postParams.get(s)));
			}
			post.setEntity(new UrlEncodedFormEntity(urlParams));	
		}
		LOG.info("Posting to URL [{}]", post.getURI());
		HttpResponse postResponse = client.execute(post);
		

		return processResponse(postResponse);
		
	}
	
	public JSONObject requestAccessToken(String url, String authToken) throws UnsupportedEncodingException, IOException{
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		
		urlParams.add(new BasicNameValuePair("code", authToken));
		urlParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
		urlParams.add(new BasicNameValuePair("client_id", clientId));
		urlParams.add(new BasicNameValuePair("client_secret", clientSecret));
		urlParams.add(new BasicNameValuePair("redirect_uri", redirect));
		
		post.setEntity(new UrlEncodedFormEntity(urlParams));
		
		HttpResponse postResponse = client.execute(post);
		
		LOG.info("Response code [{}]", postResponse.getStatusLine());

		return processResponse(postResponse);
	}
	
	private JSONObject processResponse(HttpResponse response) throws IOException{
		
		LOG.info("Response code [{}]", response.getStatusLine());
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		LOG.debug(result.toString());
		return new JSONObject(result.toString());
	}
}
