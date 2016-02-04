package com.tumblr.apps.utils;

public interface Constants {
	String DEFAULT_USER_CONSUMER_KEY = "cjgzLu4eVDsvCrafKGHAwlsk4ZAjtOpzlmaL1uKg2U5MDhu0o7";
	String DEFAULT_USER_CONSUMER_SECRET = "d1jXXBCPuNW002VFj0sraGXbns3NhOYPVusdmfprOAFmquEWa0";
	
	int MAX_PHOTO_SPACE = 4 * 1024 * 1024;
	
	int REFRESH_TIME = 3 * 60 * 60; // refresh every 3 h
	
	String BASE_URL = "http://api.tumblr.com/v2";
	
	String OAUTH_URL = "oauth://tumblrsnap";
}