package com.next.eswaraj.volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.eswaraj.web.dto.device.RegisterGcmDeviceId;
import com.google.gson.Gson;
import com.next.eswaraj.config.Constants;

public class RegisterGcmRequest extends Request<String> {

	private final Response.Listener<String> mListener;
    HttpEntity entity;

    public RegisterGcmRequest(Response.ErrorListener errorListener, Response.Listener<String> listener, RegisterGcmDeviceId registerGcmDeviceId) throws UnsupportedEncodingException {
        super(Method.POST, Constants.URL_POST_SAVE_GCM_ID, errorListener);

        entity = new StringEntity(new Gson().toJson(registerGcmDeviceId));
		mListener = listener;
	}
	
	@Override
	public String getBodyContentType() {
        return "application/json";
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	 @Override
	    protected Response<String> parseNetworkResponse(NetworkResponse response) {
	        try {
	        	Log.i("eswaraj", "Status Code = "+response.statusCode);
	            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	            Log.i("eswaraj", "json response = "+json);
	            return Response.success(
	                    json, HttpHeaderParser.parseCacheHeaders(response));
	        } catch (UnsupportedEncodingException e) {
	            return Response.error(new ParseError(e));
	        } 
	    }

	@Override
	protected void deliverResponse(String response) {
		Log.i("eswaraj", " response = "+response);
		if(mListener != null){
			mListener.onResponse(response);	
		}
		
	}
}