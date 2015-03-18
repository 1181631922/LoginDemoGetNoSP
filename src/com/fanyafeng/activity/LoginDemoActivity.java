package com.fanyafeng.activity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.fanyafeng.bean.UserBean;
import com.fanyafeng.logindemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDemoActivity extends BaseActivity implements OnClickListener {
	/** Called when the activity is first created. */
	private Button loginBtn, registerBtn;
	private EditText inputUsername, inputPassword;
	private ProgressDialog mDialog;
	private String responseMsg = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// 初始化视图控件
		initView();
		// 初始化数据
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// 检查网络
		CheckNetworkState();
	}

	private void initView() {
		loginBtn = (Button) findViewById(R.id.login_btn_login);
		loginBtn.setOnClickListener(this);

		registerBtn = (Button) findViewById(R.id.login_btn_zhuce);
		registerBtn.setOnClickListener(this);

		inputUsername = (EditText) findViewById(R.id.login_edit_account);
		inputPassword = (EditText) findViewById(R.id.login_edit_pwd);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		// 登录
		case R.id.login_btn_login:

			mDialog = new ProgressDialog(LoginDemoActivity.this);
			mDialog.setTitle("登陆");
			mDialog.setMessage("正在登陆服务器，请稍后...");
			mDialog.show();
			Thread loginThread = new Thread(new LoginThread());

			loginThread.start();

			break;
		// 注册
		case R.id.login_btn_zhuce:

			Intent intent = new Intent();
			intent.setClass(LoginDemoActivity.this, RegisterActivity.class);
			startActivity(intent);

		default:
			break;
		}

	}

	private boolean loginServer(String username, String password)
			throws UnsupportedEncodingException {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现

		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("name", username);
		params.put("password", password);

		StringBuilder url = new StringBuilder(loginUrl);
		url.append("?");

		for (Map.Entry<String, String> entry : params.entrySet()) {
			url.append(entry.getKey()).append("=");
			url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			url.append("&");
		}

		// 删掉最后一个&
		url.deleteCharAt(url.length() - 1);

		String URL = url.toString();

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet request;

		try {

			// 设置请求参数项
			request = new HttpGet(new URI(URL));

			HttpResponse response = httpClient.execute(request);

			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				loginValidate = true;
				// 获得响应信息

				HttpEntity entity = response.getEntity();

				if (entity != null) {

					String out = EntityUtils.toString(entity, "UTF-8");// out为获取的服务器返回的数据
					Log.i(out, out);

					JSONObject jsonObject = new JSONObject(out);

					int state;
					String msg;

					state = jsonObject.getInt("state");

					if (state == 0) {
						responseMsg = state + "";
						Log.i(state + "", state + "");

						JSONObject object = jsonObject.getJSONObject("data");
						userBean.setId(object.getLong("id"));
						Log.i(out, object.getLong("id") + "");

					}
					msg = jsonObject.getString("msg");
					Log.i(msg, msg);

				}

				System.out.println(responseMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginValidate;
	}

	// 检查网络状态
	public void CheckNetworkState() {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return;
		showTips();
	}

	private void showTips() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("没有可用网络");
		builder.setMessage("当前网络不可用，是否设置网络？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 如果没有网络连接，则进入网络设置界面
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				LoginDemoActivity.this.finish();
			}
		});
		builder.create();
		builder.show();
	}

	// Handler
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();

				Toast.makeText(getApplicationContext(), "登录成功！",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(LoginDemoActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "URL验证失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	// LoginThread线程类
	class LoginThread implements Runnable {

		@Override
		public void run() {
			String username = inputUsername.getText().toString();
			String password = inputPassword.getText().toString();

			// URL合法，但是这一步并不验证密码是否正确
			boolean loginValidate = false;
			try {
				loginValidate = loginServer(username, password);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message msg = handler.obtainMessage();
			if (loginValidate) {
				if (responseMsg.equals("0")) {
					msg.what = 0;
					handler.sendMessage(msg);
				} else {
					msg.what = 1;
					handler.sendMessage(msg);
				}

			} else {
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}

	}

	/**
	 * MD5单向加密，32位，用于加密密码，因为明文密码在信道中传输不安全，明文保存在本地也不安全
	 * 
	 * @param str
	 * @return
	 */

}
