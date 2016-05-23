package com.gm.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.util.JSON;

public class HttpClientManager {

	private static HttpClient client;;

	static Log log = LogFactory.getLog(HttpClientManager.class);

	public static HttpClient getClient() {
		if (client == null) {
			synchronized (HttpClientManager.class) {
				if (client == null) {
					client = new HttpClient();
				}
			}
		}
		return client;
	}

	public static String executePostMethod(String host, NameValuePair[] pairs) {
		HttpMethod method = new HeadMethod(host); // new Utf8PostMethod(host);
		method.setQueryString(pairs);
		int returnValue = -1;
		try {
			returnValue = getClient().executeMethod(method);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String obj = "";
		try {
			obj = method.getResponseBodyAsString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			log.info("返回的数据:" + obj.toString() + ",执行的返回码:" + returnValue + ",url:" + method.getURI().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static String executeGetMethod(String host, NameValuePair[] pairs) {
		HttpMethod method = new UtfGetMethod(host);
		method.setQueryString(pairs);
		int returnValue = -1;
		try {
			returnValue = getClient().executeMethod(method);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String obj = "";
		try {
			obj = method.getResponseBodyAsString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			log.info("返回的数据:" + obj.toString() + ",执行的返回码:" + returnValue + ",url:" + method.getURI().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;

	}

	public static NameValuePair[] getUri(String host, String key, int ignoreIndex, Object... objs) {

		NameValuePair[] nameValuePairs = new NameValuePair[objs.length / 2 + 1];
		int index = 0;
		String md5 = "";
		for (int i = 0; i < objs.length - 1; i++) {
			int begin = i;
			int value = ++i;
			nameValuePairs[index] = new NameValuePair(objs[begin].toString().trim(), objs[value].toString().trim());
			index++;
			if (--ignoreIndex >= 0) {
				continue;
			}
			md5 += objs[value].toString().trim();
		}
		md5 += key;
		nameValuePairs[nameValuePairs.length - 1] = new NameValuePair("sign", Utils.md5(md5));
		return nameValuePairs;
	}

	/**
	 * 获得链接的参数
	 * 
	 * @param objs
	 * @return
	 */
	public static NameValuePair[] pairs(Object... objs) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		for (int i = 0; i < objs.length - 1; i++) {
			int begin = i;
			int end = ++i;
			nameValuePair.add(new NameValuePair(objs[begin].toString().trim(), objs[end].toString().trim()));
		}

		NameValuePair[] pairs = new NameValuePair[nameValuePair.size()];

		return (NameValuePair[]) nameValuePair.toArray(pairs);
	}

	public static String postMessage(int ignoreIndex, String host, String key, Object... objs) {
		NameValuePair[] nameValuePairs = getUri(host, key, ignoreIndex, objs);
		return executePostMethod(host, nameValuePairs);
	}

	public static String postMessage(String host, Object... objs) {
		NameValuePair[] nameValuePairs = pairs(objs);
		return executePostMethod(host, nameValuePairs);
	}

	public static String postMessage4DarkNight(String host, Object... args) {
		Utf8PostMethod method = new Utf8PostMethod(host);
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < args.length - 1; i++) {
			if (i % 2 == 0) {
				params.put(args[i].toString(), args[i + 1]);
			}
		}
		String jparam = JSON.serialize(params);
		jparam = string2Unicode(jparam);
		method.addRequestHeader("Content-Length", jparam.length() + "");
		method.addRequestHeader("Referer", "/flandy/post.php");
		method.addRequestHeader("Host", "192.168.1.221");
		method.addRequestHeader("Connection", "Close\r\n\r\n" + jparam);
		method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
		try {
			getClient().executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);
			if (c >= 0x4E00 && c <= 0x9FA5) {// 根据字节码判断
				// 转换为unicode
				unicode.append("\\u" + Integer.toHexString(c));
			} else {
				unicode.append(c);
			}
		}

		return unicode.toString();
	}

	public static void main(String[] args) {

	}
}
