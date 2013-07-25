package com.jansampark.vashisthg.volley;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

public class JsonRequestWithCache extends JsonObjectRequest {

	public JsonRequestWithCache(int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {				
		try {
			String jsonString = new String(response.data);
			return Response.success(new JSONObject(jsonString), parseIgnoreCacheHeaders(response));
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
