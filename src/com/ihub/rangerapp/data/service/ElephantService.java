package com.ihub.rangerapp.data.service;

import java.util.Map;

import com.loopj.android.http.AsyncHttpResponseHandler;

public interface ElephantService {

	public Map<String, Object> save(
		Integer id,
		String toolUsed,
		Integer noOfAnimals,
		Integer maleCount,
		Integer femaleCount,
		Integer adultsCount,
		Integer semiAdultsCount,
		Integer juvenileCount,
		String ivoryPresence,
		String actionTaken,
		String extraNotes,
		String imagePath,
		String waypoint,
		String ranch,
		Integer leftTuskWeight,
		Integer rightTuskWeight
	);
	
	public void sync(Integer id, AsyncHttpResponseHandler handler);
}