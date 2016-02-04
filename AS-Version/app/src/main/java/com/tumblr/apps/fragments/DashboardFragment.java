package com.tumblr.apps.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tumblr.apps.PhotosAdapter;
import com.tumblr.apps.R;
import com.tumblr.apps.TumblrClient;
import com.tumblr.apps.models.Blog;
import com.loopj.android.http.JsonHttpResponseHandler;

public class DashboardFragment extends Fragment {
	private static final int TAKE_PHOTO_CODE = 1;
	private static final int PICK_PHOTO_CODE = 2;
	private static final int POST_PHOTO_CODE = 4;
	
	private static final String TAG = DashboardFragment.class.getSimpleName();
	
	private Uri photoUri;
	private Bitmap photoBitmap;
	
	TumblrClient client;
	ArrayList<Blog> photos;
	PhotosAdapter photosAdapter;
	ListView lvPhotos;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photos, container, false);
		setHasOptionsMenu(true);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		client = ((TumblrClient) TumblrClient.getInstance(
				TumblrClient.class, getActivity()));
		photos = new ArrayList<Blog>();
		photosAdapter = new PhotosAdapter(getActivity(), photos);
		lvPhotos = (ListView) getView().findViewById(R.id.lvPhotos);
		lvPhotos.setAdapter(photosAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		reloadPhotos();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.photos, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_take_photo:
			{
				// Take the user to the camera app
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				photoUri = Uri.fromFile(getOutputMediaFile());
				camera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				// Camera will save the photo taken in photoUri
				startActivityForResult(camera, TAKE_PHOTO_CODE);
			}
			break;
			case R.id.action_use_existing:
			{
				// Take the user to the gallery app
				Intent existingPhoto = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(existingPhoto, PICK_PHOTO_CODE);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			
		case PICK_PHOTO_CODE:	// pick photo
			if (resultCode == Activity.RESULT_OK) {
				photoUri = data.getData();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity(), R.string.error_canceled, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.error_pick, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case POST_PHOTO_CODE:
			reloadPhotos();
			break;
		}
	}
	
	private void reloadPhotos() {
		client.getTaggedPhotos(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int code, JSONObject response) {
				try {
					JSONArray photosJson = response.getJSONArray("response");
					photosAdapter.clear();
					photosAdapter.addAll(Blog.fromJson(photosJson));
					mergeUserBlogsByTimeStamp();
					Log.e(TAG, "Parse success");
				} catch (JSONException e) {
					Log.e(TAG, "Parse failed");
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d(TAG, arg0.toString());
			}
		});
	}
	
	private static File getOutputMediaFile() {
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "tumblrsnap");
	    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
	        return null;
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
	
	// Loads feed of users photos and merges them with the tagged photos
	// Used to avoid an API limitation where user photos arent returned in tagged
	private void mergeUserBlogsByTimeStamp() {
		client.getUserPhotos(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int code, JSONObject response) {
				try {
					JSONArray photosJson = response.getJSONObject("response").getJSONArray("posts");
					for (Blog p : Blog.fromJson(photosJson)) {
						if (p.isSnap()) { photosAdapter.add(p); }
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				photosAdapter.sort(new Comparator<Blog>() {
					@Override
					public int compare(Blog a, Blog b) {
						return Long.valueOf(b.getTimestamp()).compareTo(a.getTimestamp());
					}
				});
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", arg0.toString());
			}
		});
	}
}
