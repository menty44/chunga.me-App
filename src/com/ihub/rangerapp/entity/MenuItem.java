package com.ihub.rangerapp.entity;

import android.graphics.drawable.Drawable;

public class MenuItem implements Comparable<Object> {
	
	String name;
	private Drawable icon = null;
	
	public MenuItem(String name, String iconName) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Object another) {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}	
}
