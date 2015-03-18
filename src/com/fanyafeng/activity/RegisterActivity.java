package com.fanyafeng.activity;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.fanyafeng.activity.LoginDemoActivity.LoginThread;
import com.fanyafeng.logindemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText newUser,newPassword,confirmPassword;
	private Button registerBtn, clearBtn;
	private ProgressDialog mDialog;
	private String responseMsg = "";
	private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时10秒钟  
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟  
	private static final int LOGIN_OK = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		newUser = (EditText)findViewById(R.id.newUser_input);
		newPassword = (EditText)findViewById(R.id.newPassword_input);
		confirmPassword = (EditText)findViewById(R.id.Confirm_input);
		registerBtn = (Button)findViewById(R.id.registerbtn);
		clearBtn = (Button)findViewById(R.id.clearbtn);
		registerBtn.setOnClickListener(new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				String newusername = newUser.getText().toString();
				String newpassword = md5(newPassword.getText().toString());
				String confirmpwd = md5(confirmPassword.getText().toString());
				
				if(newpassword.equals(confirmpwd))
				{
					SharedPreferences sp = getSharedPreferences("userdata",0);
					Editor editor = sp.edit();
					editor.putString("username", newusername);
					editor.putString("password", newpassword);
					editor.commit();
					mDialog = new ProgressDialog(RegisterActivity.this);
					mDialog.setTitle("登陆");
					mDialog.setMessage("正在登陆服务器，请稍后...");
					mDialog.show();
					Thread loginThread = new Thread(new RegisterThread());
					loginThread.start();
					
				}else
				{
					Toast.makeText(getApplicationContext(), "您两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		clearBtn.setOnClickListener(new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				newUser.setText("");
				newPassword.setText("");
				confirmPassword.setText("");
			}
			
		});
	}
	
	//初始化HttpClient，并设置超时
    public HttpClient getHttpClient()
    {
    	BasicHttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
    	HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
    	HttpClient client = new DefaultHttpClient(httpParams);
    	return client;
    }
	
	 private boolean registerServer(String username, String password)
	    {
	    	boolean loginValidate = false;
	    	//使用apache HTTP客户端实现
	    	String urlStr = "http://192.168.1.101:8080/LoginServlet/RegisterServlet";
	    	HttpPost request = new HttpPost(urlStr);
	    	//如果传递参数多的话，可以丢传递的参数进行封装
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	//添加用户名和密码
	    	params.add(new BasicNameValuePair("username",username));
	    	params.add(new BasicNameValuePair("password",password));
	    	try
	    	{
	    		//设置请求参数项
	    		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	    		HttpClient client = getHttpClient();
	    		//执行请求返回相应
	    		HttpResponse response = client.execute(request);
	    		
	    		//判断是否请求成功
	    		if(response.getStatusLine().getStatusCode()==200)
	    		{
	    			loginValidate = true;
	    			//获得响应信息
	    			responseMsg = EntityUtils.toString(response.getEntity());
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return loginValidate;
	    }
	 
	//Handler
	    Handler handler = new Handler()
	    {
	    	public void handleMessage(Message msg)
	    	{
	    		switch(msg.what)
	    		{
	    		case 0:
	    			mDialog.cancel();
	    			showDialog("注册成功！");
	    			break;
	    		case 1:
	    			mDialog.cancel();
	    			Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
	    			break;
	    		case 2:
	    			mDialog.cancel();
	    			Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
	    			break;
	    		
	    		}
	    		
	    	}
	    };
	    
	    
	    //RegisterThread线程类
	    class RegisterThread implements Runnable
	    {

			@Override
			public void run() {
				String username = newUser.getText().toString();
				String password = md5(newPassword.getText().toString());
					
				//URL合法，但是这一步并不验证密码是否正确
		    	boolean registerValidate = registerServer(username, password);
		    	//System.out.println("----------------------------bool is :"+registerValidate+"----------response:"+responseMsg);
		    	Message msg = handler.obtainMessage();
		    	if(registerValidate)
		    	{
		    		if(responseMsg.equals("success"))
		    		{
		    			msg.what = 0;
			    		handler.sendMessage(msg);
		    		}else
		    		{
		    			msg.what = 1;
			    		handler.sendMessage(msg);
		    		}
		    		
		    	}else
		    	{
		    		msg.what = 2;
		    		handler.sendMessage(msg);
		    	}
			}
	    	
	    }
	
	private void showDialog(String str)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("注册");
		builder.setMessage(str);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginDemoActivity.class);
				startActivity(intent);
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * MD5单向加密，32位，用于加密密码，因为明文密码在信道中传输不安全，明文保存在本地也不安全  
	 * @param str
	 * @return
	 */
    public static String md5(String str)  
    {  
        MessageDigest md5 = null;  
        try  
        {  
            md5 = MessageDigest.getInstance("MD5");  
        }catch(Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++)  
        {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++)  
        {  
            int val = ((int)md5Bytes[i])&0xff;  
            if(val < 16)  
            {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }  
      
}
