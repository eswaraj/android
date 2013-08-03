package com.jansampark.vashisthg.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jansampark.vashisthg.R;

public class YouTubeVideoHelper {
	RequestQueue requestQueue;

	public interface YouTubeLinkRequestCallback {
		public void youTubeLinksSuccess();

		public void youTubeLinksFailure();
	}

	Context context;
	YouTubeLinkRequestCallback callback;

	private static final String YOUTUBE_LINK_URL = "http://50.57.224.47/html/dev/micronews/get_video_links.php ";
	private static final String PREF = "pref_youtube";

	private static final String ABOUT_ALL = "about";

	public YouTubeVideoHelper(Context context) {
		this.context = context.getApplicationContext();
	}

	public YouTubeVideoHelper(Context context,
			YouTubeLinkRequestCallback callback) {
		this.context = context;
		this.callback = callback;
	}

	public void downloadYouTubeLinks() {
		requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				YOUTUBE_LINK_URL, null, createYoutubeLinkSuccessListener(),
				createYoutubeLinkErrorListener());
		requestQueue.add(request);

	}

	private Response.Listener<JSONObject> createYoutubeLinkSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				Editor editor = getSharedPreferences();
				try {
					Resources res = context.getResources();

					String about = jsonObject.getString(ABOUT_ALL);

					editor.putString(ABOUT_ALL, about);
					int[] issueIds = res.getIntArray(R.array.basic_items_index);

					for (int i = 0; i < issueIds.length; i++) {
						String issueId = issueIds[i] + "";
						putUrlFor(jsonObject, editor, issueId);
					}

					if (null != callback) {
						callback.youTubeLinksSuccess();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					editor.commit();
				}
			}
		};
	}

	private void putUrlFor(JSONObject jsonObject, Editor editor, String issueId)
			throws JSONException {
		String url = jsonObject.getString(issueId);
		editor.putString(issueId, url);
	}

	private Editor getSharedPreferences() {
		SharedPreferences pref = context.getSharedPreferences(PREF, 0);
		return pref.edit();
	}

	private Response.ErrorListener createYoutubeLinkErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (null != callback) {
					callback.youTubeLinksFailure();
				}
			}
		};
	}

	public String getLinkForIssueId(int issueId) {
		SharedPreferences pref = context.getSharedPreferences(PREF, 0);
		return pref.getString(issueId + "", "");
	}

	public String getLinkForAll() {
		SharedPreferences pref = context.getSharedPreferences(PREF, 0);
		return pref.getString(ABOUT_ALL, "");
	}

}
