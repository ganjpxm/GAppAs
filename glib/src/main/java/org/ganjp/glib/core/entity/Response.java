/**
 * Response.java
 * 
 * Created by Gan Jianping on 20/06/13.
 * Copyright (c) 2013 Gan Jianping. All rights reserved.
 */
package org.ganjp.glib.core.entity;

public class Response {
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAIL = "fail";
	
	private String result;
	private String uuid;
	private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
