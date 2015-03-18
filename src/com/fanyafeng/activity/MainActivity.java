package com.fanyafeng.activity;

import com.fanyafeng.bean.UserBean;
import com.fanyafeng.logindemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends LoginDemoActivity {

	private TextView home;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		this.home = (TextView)MainActivity.this.findViewById(R.id.home);
		this.home.setText("用户id:"+userBean.getId());
	}

}
