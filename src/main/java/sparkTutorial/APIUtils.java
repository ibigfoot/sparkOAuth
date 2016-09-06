package sparkTutorial;

import java.io.BufferedReader;
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

	/* I use two different dev orgs for this, one configured to my localhost environment
	 * and one configured to my heroku environment. The configuration is in the connected
	 * app for the OAuth callback (Salesforce allows http://localhost)
	 * troy@documenter.org - prod callback
	 * troy@documenter.local - localhost callback
	 */
	private String clientId = null;
	private String redirect = null;
	private String clientSecret = null;
	
	private static Logger LOG = LoggerFactory.getLogger(APIUtils.class); 
	
	public static final String AUTH_TOKEN = "auth_token";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REDIRECT_URI = "redirect_uri";
	
	public APIUtils () {
		
		clientId = System.getenv(CLIENT_ID);
		redirect = System.getenv(REDIRECT_URI); 
		clientSecret = System.getenv(CLIENT_SECRET); 
		
		LOG.info("{} [{}] {} [{}] {} [{}]", CLIENT_ID, clientId, CLIENT_SECRET, clientSecret, REDIRECT_URI, redirect);
		
		if(clientId == null || redirect == null || clientSecret == null) {
			throw new RuntimeException("Unable to configure OAuth parameters. Check you system properties contain "+CLIENT_ID+" : "+REDIRECT_URI+" : "+CLIENT_SECRET);
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
