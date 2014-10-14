package com.next.eswaraj.volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;

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
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.next.eswaraj.config.Constants;

public class RegisterFacebookUserRequest extends Request<String> {

	private final Response.Listener<String> mListener;
    HttpEntity entity;

    JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == null ? null : new Date(json.getAsLong());
        }
    };
    JsonSerializer<Date> ser = new JsonSerializer<Date>() {

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    };

    private Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).registerTypeAdapter(Date.class, deser).create();

    public RegisterFacebookUserRequest(Response.ErrorListener errorListener, Response.Listener<String> listener, RegisterFacebookAccountRequest registerFacebookAccountRequest)
            throws UnsupportedEncodingException {
        super(Method.POST, Constants.URL_POST_SAVE_FACEBOOK_USER, errorListener);
        String json = gson.toJson(registerFacebookAccountRequest);
        entity = new StringEntity(json);
        Log.i("eswaraj", "json : " + json);
		mListener = listener;
	}
	
	@Override
	public String getBodyContentType() {
        Log.i("eswaraj", "Content Type : " + entity.getContentType().getValue());
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