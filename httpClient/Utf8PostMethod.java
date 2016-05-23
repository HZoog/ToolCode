package com.gm.comm;

import org.apache.commons.httpclient.methods.PostMethod;

public class Utf8PostMethod extends PostMethod {

	public Utf8PostMethod(String url) {
		super(url);
	}

	@Override
	public String getRequestCharSet() {
		return "UTF-8";
	}

	@Override
	public String getResponseCharSet() {
		return "UTF-8";
	}

}
