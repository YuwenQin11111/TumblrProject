package com.tumblr.apps;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TumblrApi;

import android.content.Context;
import android.graphics.Bitmap;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.tumblr.apps.utils.Constants;

public class TumblrClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TumblrApi.class;
    public static final String REST_CONSUMER_KEY = "BcnUeYPIxBaVCz5sYcs4SkytRqM8azgLclb1PUpeFcknic9RYY";
    public static final String REST_CONSUMER_SECRET = "FoiHHbknPRFeyiaBpOxizSGzbYsflp6DiFfwBi85kCYEznKGGh";

    public TumblrClient(Context context) {
        super(context, REST_API_CLASS, Constants.BASE_URL, REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET, Constants.OAUTH_URL);
    }

    public void getTaggedPhotos(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("tag", "cptumblrsnap");
        params.put("limit", "20");
        params.put("api_key", REST_CONSUMER_KEY);
        client.get(getApiUrl("tagged"), params, handler);
    }
    
    public void getUserPhotos(AsyncHttpResponseHandler handler) {
    	RequestParams params = new RequestParams();
        params.put("type", "photo");
        params.put("limit", "20");
        params.put("api_key", REST_CONSUMER_KEY);
        client.get(getApiUrl("user/dashboard"), params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        client.get(getApiUrl("user/info"), null, handler);
    }
    
    public void createPhotoPost(String blog, File file, AsyncHttpResponseHandler handler) {
    	RequestParams params = new RequestParams();
    	params.put("type", "photo");
    	params.put("tags", "cptumblrsnap");
    	try { 
			params.put("data", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	client.post(getApiUrl(String.format("blog/%s/post?type=photo&tags=cptumblrsnap", blog)), params, handler);
    }
      
    public void createPhotoPost(String blog, Bitmap bitmap, final AsyncHttpResponseHandler handler) {   
        RequestParams params = new RequestParams();
    	params.put("type", "photo");
    	params.put("tags", "cptumblrsnap");
    	
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] bytes = stream.toByteArray();
    	params.put("data", new ByteArrayInputStream(bytes), "image.png");
    	
    	client.post(getApiUrl(String.format("blog/%s/post?type=photo&tags=cptumblrsnap", blog)), params, handler);
    }
}
