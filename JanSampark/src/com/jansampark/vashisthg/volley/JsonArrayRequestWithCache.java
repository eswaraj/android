package com.jansampark.vashisthg.volley;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

public class JsonArrayRequestWithCache extends JsonArrayRequest {

	

	

	public JsonArrayRequestWithCache(String url, Listener<JSONArray> listener,
			ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {				
		try {
			String jsonString = new String(response.data);
			return Response.success(new JSONArray(jsonString), parseIgnoreCacheHeaders(response));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
	}
	
	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
	    long now = System.currentTimeMillis();

	    Map<String, String> headers = response.headers;
	    long serverDate = 0;
	    String serverEtag = null;
	    String headerValue;

	    headerValue = headers.get("Date");
	    if (headerValue != null) {
	        serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
	    }

	    serverEtag = headers.get("ETag");

	    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
	    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
	    final long softExpire = now + cacheHitButRefreshed;
	    final long ttl = now + cacheExpired;

	    Cache.Entry entry = new Cache.Entry();
	    entry.data = response.data;
	    entry.etag = serverEtag;
	    entry.softTtl = softExpire;
	    entry.ttl = ttl;
	    entry.serverDate = serverDate;
	    entry.responseHeaders = headers;

	    return entry;
	}

}
