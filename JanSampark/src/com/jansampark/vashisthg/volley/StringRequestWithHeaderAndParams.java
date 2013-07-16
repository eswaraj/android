package com.jansampark.vashisthg.volley;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

public class StringRequestWithHeaderAndParams extends StringRequest {
	
	private final Map<String, String> headers;
	private final Map<String, String> params;
    

	public StringRequestWithHeaderAndParams(int method, String url, Listener<String> listener, ErrorListener errorListener, Map<String, String> headers, Map<String, String> params) {
		super(method, url, listener, errorListener);
		this.headers = headers;
		this.params = params;
	}
	

	
	
	public StringRequestWithHeaderAndParams(String url, Listener<String> listener, ErrorListener errorListener, Map<String, String> headers, Map<String, String> params) {
		super(url, listener, errorListener);
		this.headers = headers;
		this.params = params;
	}




	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {		
		return params != null ? params : super.getParams();
	}

}
