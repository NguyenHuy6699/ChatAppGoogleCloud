package com.huy.dto.output;

import com.huy.dto.BaseDTO;

public class Contact extends BaseDTO {
	private String name;
	private String avatarUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
