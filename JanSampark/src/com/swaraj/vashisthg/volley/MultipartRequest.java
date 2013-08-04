package com.swaraj.vashisthg.volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.text.TextUtils;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.swaraj.vashisthg.IssueDetailsActivity.IssueDetail;

public class MultipartRequest extends Request<String> {

	private MultipartEntity entity = new MultipartEntity();


	
	private static final String LAT = "lat";
	private static final String LON = "long";
	private static final String ISSUE_TYPE = "issue_type";
	private static final String TEMPLATE = "issue_tmpl_id";
	private static final String DESCIPTION = "txt";
	private static final String IMG = "img";
	private static final String USER_IMG = "profile_img";
	private static final String REPORTER_ID = "reporter_id";
	
	

	private final Response.Listener<String> mListener;
	private  File issueImage;
	private  File userImage;
	private  String lat;
	private  String lon;
	private String issueType;
	private String template;
	private String reporterId;
	private String description;

	public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, IssueDetail issueDetail) {
		super(Method.POST, url, errorListener);

		mListener = listener;
		
		lat = issueDetail.lat ;
		lon = issueDetail.lon;
		issueType = issueDetail.issueItem.getIssueCategory() + "";
		template = issueDetail.issueItem.getTemplateId() + "";
		reporterId = issueDetail.reporterId;
		addIssueDetailImage(issueDetail.image);
		addUserImage(issueDetail.userImage);
		
		description = issueDetail.description;
		buildMultipartEntity();

	}
	
	private void addIssueDetailImage(String issueDetailImage) {
		if(!TextUtils.isEmpty(issueDetailImage) ) {
			issueImage = new File(issueDetailImage);
			if(!issueImage.exists()) {
				issueImage = null;
			}
		}
	}
	
	private void addUserImage(String userImage) {
		if (!TextUtils.isEmpty(userImage)) {
			this.userImage = new File(userImage);
			if(!this.userImage.exists()){
				userImage = null;
			}
		}
	}
	
	

	private void buildMultipartEntity() {
		if( null != issueImage) {
			entity.addPart(IMG, new FileBody(issueImage));
		}
		if (null != userImage) {
			entity.addPart(USER_IMG, new FileBody(userImage));
		}
		try {
			entity.addPart(LAT, new StringBody(lat));
			entity.addPart(LON, new StringBody(lon));
			entity.addPart(DESCIPTION, new StringBody(description));
			entity.addPart(ISSUE_TYPE, new StringBody(issueType));
			entity.addPart(TEMPLATE, new StringBody(template));
			entity.addPart(REPORTER_ID, new StringBody(reporterId));
		} catch (UnsupportedEncodingException e) {
			VolleyLog.e("UnsupportedEncodingException");
		}

	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
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
	            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	            return Response.success(
	                    json, HttpHeaderParser.parseCacheHeaders(response));
	        } catch (UnsupportedEncodingException e) {
	            return Response.error(new ParseError(e));
	        } 
	    }

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
}