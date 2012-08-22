package il.reporter.gws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.util.Log;

public class WebRequest {

	public void getInternetData(JSONStringer jsonStr, String urlStr)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException {
		// HttpPost request = new
		// HttpPost("http://www.smokingnot2012.appspot.com/UpdateUser");
		HttpPost request = new HttpPost(urlStr);
		try {
			StringEntity entity = new StringEntity(jsonStr.toString(), "UTF-8");
			InputStream is = null;
			entity.setContentType("application/json;charset=UTF-8;"); // text/plain;charset=UTF-8
																		// (can
																		// be
																		// either)
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			request.setEntity(entity);

			is = request.getEntity().getContent();
			Log.i("ERIC ortal4", convertStreamToString(is));

			// Send request to WCF service
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// parse all data(byte wise) from rd
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;

		// read bytes
		while ((cp = rd.read()) != -1) {
			// build string
			sb.append((char) cp);
		}
		return sb.toString();
	}

	// given a URL, read a and return the JsonObject posted to it(given that it
	// was
	// properly posted
	// @SuppressWarnings("finally")
	public JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {

		try {
			//String encodedStr = URLEncoder.encode(url, "UTF-8");
			Log.i("ERIC", url);

			URL mUrl = new URL(url);
			URLConnection conn = mUrl.openConnection();
			// InputStream is = new URL(url).openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} catch (Exception e) {
			Log.e("error in read json from url", e.toString());
		} finally {
			//is.close();
		}

		return null;
	}

	public void SendEmail(String urlStr) throws URISyntaxException,
			ClientProtocolException, IOException {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String newUrlStr = urlStr.replace(" ", "%20").replace("\n", "%20");
			Log.i("ERIC ortal", newUrlStr);
			URI website = new URI(newUrlStr);
			HttpGet request = new HttpGet();
			request.setURI(website);
			HttpResponse res = httpClient.execute(request);
		} catch (Exception e) {
			Log.w("error while sending email to database-webrequest",
					e.getMessage());
			e.printStackTrace();
		}
	}

	String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}
}
