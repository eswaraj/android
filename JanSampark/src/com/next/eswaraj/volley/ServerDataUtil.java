package com.next.eswaraj.volley;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.next.eswaraj.JanSamparkApplication;
import com.next.eswaraj.R;

import de.greenrobot.event.EventBus;

public class ServerDataUtil {

    JanSamparkApplication application;
    List<CategoryWithChildCategoryDto> categoryList;

    private ServerDataUtil() {
        // TODO Auto-generated constructor stub
    }

    private static ServerDataUtil instance = new ServerDataUtil();

    public static ServerDataUtil getInstance() {
        return instance;
    }

    public void initData(JanSamparkApplication application) {
        this.application = application;
        initCategories(application);
    }


    private void initCategories(JanSamparkApplication application) {
        String requestTag = "GetCategory";
        String url = "http://dev.admin.eswaraj.com/eswaraj-web/mobile/categories";
        JsonArrayRequest request = new JsonArrayRequest(url, createCategoryReqSuccessListener(), createMyReqErrorListener());
        application.submitServerRequest(requestTag, request);
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(application, R.string.network_error, Toast.LENGTH_LONG).show();
                Log.e("eswaraj", "Unable to connect to service", error);
            }
        };
    }

    private Response.Listener<JSONArray> createCategoryReqSuccessListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonObject) {
                try {
                    Log.i("eswaraj", "Success for categories");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CategoryWithChildCategoryDto>>() {
                    }.getType();
                    categoryList = gson.fromJson(jsonObject.toString(), listType);
                    EventBus.getDefault().post(categoryList);
                    // hideProgressBar();
                } catch (Exception e) {
                    Log.e("Error", "Error occured", e);
                }
            }

        };
    }

    public List<CategoryWithChildCategoryDto> getCategoryList() {
        return categoryList;
    }
}
