package com.ihub.rangerapp.data.service;

import java.util.Map;

import com.loopj.android.http.AsyncHttpResponseHandler;

public interface WaterholeService {

	public Map<String, Object> save(
			Integer id,
			String name,
			String level,
			Integer noOfAnimalsSeen,
			String extraNotes,
			String imagePath,
			String waypoint);
	
	public void sync(Integer id, AsyncHttpResponseHandler handler);
}
