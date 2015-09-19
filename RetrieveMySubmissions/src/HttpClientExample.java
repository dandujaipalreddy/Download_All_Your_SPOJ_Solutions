import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HttpClientExample {

	private final static String USER_AGENT = "Mozilla/5.0";
	private static String cookie="";
	private static String username;
	private static String password;
	private static Map<String,String> sub=new HashMap<String,String>();
	public static void main(String[] args) throws Exception {

		HttpClientExample http = new HttpClientExample();
		Scanner in=new Scanner(System.in);
		System.out.println("Enter Your UserName");
		username=in.nextLine();
		System.out.println("Enter Your UserName");
		password=in.nextLine();

		if(http.authenticateUser("http://www.spoj.com/login", username, password)){
			
			http.sendGet();
			getAllSubmissions();
		
			
		}
		else{
			
			System.out.println("Try With Correct Credentials!!!");
		}
	}


	private boolean sendGet() throws Exception {

		String url = "http://www.spoj.com/status/"+username+"/signedlist/";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.addHeader("Cookie",cookie);
		HttpResponse response = client.execute(request);
		if(response.getStatusLine().getStatusCode()!=200) return false;
		Header[] a=response.getAllHeaders();
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		int count=0;
		while ((line = rd.readLine()) != null) {
			if(++count<10) continue;
				StringTokenizer st=new StringTokenizer(line,"|");
				int c=0;
				String subid="",substate="",problemName="";
				while(st.hasMoreTokens()) {
					if(c==0)  subid=st.nextToken().toString().trim();
					else if(c==2)	problemName=st.nextToken().toString().trim();
					else if(c==3)	substate=st.nextToken().toString().trim();
					else st.nextToken();
					c++;
					
				}
				
				System.out.println(subid+" "+substate+" "+problemName);
				if(substate.equalsIgnoreCase("AC")) sub.put(subid,problemName);
		}
		return true;

	}


	static boolean authenticateUser(String url,String username,String Password) throws ClientProtocolException, IOException{
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setHeader("User-Agent", USER_AGENT);
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("next_raw", "/"));
		urlParameters.add(new BasicNameValuePair("autologin", "1"));
		urlParameters.add(new BasicNameValuePair("login_user",username));
		urlParameters.add(new BasicNameValuePair("password",Password));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		
		HttpResponse response = client.execute(post);
		if(response.getStatusLine().getStatusCode()!=302) return false;
		Header[] a=response.getAllHeaders();
		for(Header i:a)
			if(i.getName().contains("Cookie")) cookie=cookie+i.getValue();
		return true;
	}
	



	static void getSubmission(String problem,String id) throws ClientProtocolException, IOException{
		
		String url = "http://www.spoj.com/submit/"+problem+"/id="+id;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.addHeader("Cookie",cookie);
		HttpResponse response = client.execute(request);
		//if(response.getStatusLine().getStatusCode()!=200) return false;
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		int count=0;
		StringBuilder result=new StringBuilder();
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		Document doc = Jsoup.parseBodyFragment(result.toString());
		Elements links = doc.select("body textarea");
		new saveToFile(problem+".txt",links.text()).saveit();;
		System.out.println(links.text());
	}
	
	static void getAllSubmissions() throws ClientProtocolException, IOException{
		
		for(Map.Entry<String, String> a:sub.entrySet()){
			getSubmission(a.getValue(),a.getKey());
			
		}
	}
	
}