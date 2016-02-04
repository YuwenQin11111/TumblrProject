package com.tumblr.apps.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Blog {
	private String id;
	private String blogName;
	private long timestamp;
	private String caption;
	private JSONArray tagsArray;
	private JSONArray photoArray;
	
	public String getId() {
		return id;
	}
	
	public String getBlogName() {
		return blogName;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getCaption() {
		return caption;
	}

	public boolean isSnap() {
		boolean isSnap = false;
		for (int i = 0; i < tagsArray.length(); i++) {
			try {
				if (tagsArray.getString(i).equals("cptumblrsnap")) { isSnap = true; }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return isSnap;
	}

    public String getAvatarUrl() {
        return String.format(
                "http://api.tumblr.com/v2/blog/%s.tumblr.com/avatar/64",
                blogName);
    }

    public String getPhotoUrl() {
        try {
            return getPhotoMeta().getString("url");
        } catch (Exception e) {
            return null;
        }
    }

    public int getPhotoHeight() {
        try {
            return getPhotoMeta().getInt("height");
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPhotoWidth() {
        try {
            return getPhotoMeta().getInt("width");
        } catch (Exception e) {
            return 0;
        }
    }

    private JSONObject getPhotoMeta() {
        try {
            JSONArray altSizes = ((JSONObject) photoArray.get(0))
                    .getJSONArray("alt_sizes");
            for (int i = 0; i < altSizes.length(); i++) {
                JSONObject altSize = (JSONObject) altSizes.get(i);
                int width = altSize.getInt("width");
                if (width < 1000) {
                    return altSize;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public static Blog fromJson(JSONObject jsonObject) {
        Blog photo = new Blog();
        try {
			photo.id = jsonObject.getString("id");
			photo.blogName = jsonObject.getString("blog_name");
			photo.timestamp = jsonObject.getLong("timestamp");
			photo.caption = jsonObject.getString("caption");
			photo.photoArray = jsonObject.getJSONArray("photos");
			photo.tagsArray = jsonObject.getJSONArray("tags");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return photo;
    }

    public static ArrayList<Blog> fromJson(JSONArray jsonArray) {
        ArrayList<Blog> photos = new ArrayList<Blog>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject photoJson = jsonArray.getJSONObject(i);
                if (photoJson.getString("type").equals("photo")) {
                    photos.add(Blog.fromJson(photoJson));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photos;
    }

}
