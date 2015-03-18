package com.fanyafeng.activity;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.fanyafeng.bean.UserBean;

import android.app.Activity;

public class BaseActivity extends Activity{

	public static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
	public static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	public static final int LOGIN_OK = 1;
	public static UserBean userBean = new UserBean();
	public static String loginUrl = "http://hhhccckkk3.jsp.fjjsp.net/hck/login";
	public static String registerUrl = "http://hhhccckkk3.jsp.fjjsp.net/hck/register";
			
	// 初始化HttpClient，并设置超时
	public static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	
}
