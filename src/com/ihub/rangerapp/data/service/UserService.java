package com.ihub.rangerapp.data.service;

import java.util.Map;

public interface UserService {

	public Map<String, Object> login(String rangerID);
	
	public Map<String, Object> logout();
}
