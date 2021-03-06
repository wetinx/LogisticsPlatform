package com.logistics.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import com.logistics.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDetailActivity extends RoboActivity {
	private final String BASE_URL = "http://219.223.189.234";
	private AsyncHttpClient httpHelper = new AsyncHttpClient(80);
	
	@InjectView(R.id.phone)
	private EditText phone;
	
	@InjectView(R.id.password)
	private EditText password;
	
	@InjectView(R.id.login)
	private Button login;
	
	@InjectView(R.id.forget)
	private Button forget;
	
	@InjectView(R.id.login_progress)
	private View mProgressView;
	
	@InjectView(R.id.rememberpassword)
	private CheckBox  mRemember;
	
	public final static int MODE_PRIVATE = 0;
	public final static int MODE_WORLD_READABLE = 1;
	private SharedPreferences sharedPreferences;  
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_detail);
		initComponent();
		
	}

	@SuppressLint("WorldReadableFiles")
	private void initComponent() {
		sharedPreferences = this.getSharedPreferences("user_info",MODE_WORLD_READABLE);
		editor = sharedPreferences.edit();
		final String phone_l = sharedPreferences.getString("phone", null);
		final String pw_l = sharedPreferences.getString("password", null);
		final Boolean checkState = sharedPreferences.getBoolean("remember", false);
		
		phone.setError(null);
		password.setError(null);
		
		// TODO Auto-generated method stub
		login.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String phone_i = phone.getText().toString();
//				String pw_i = password.getText().toString();
//				if(phone_i.equals(phone_l) && pw_i.equals(pw_l)){				
//				Intent intent = new Intent();
//				intent.setClass(LoginDetailActivity.this,MainActivity.class);
//				startActivity(intent);
//				finish();
//				onDestroy();}
//				else{
//					String log = "手机号或者密码错误";
//					Toast.makeText(LoginDetailActivity.this, log, Toast.LENGTH_LONG).show();
//				}
				try {
					showProgress(true);
					attemptLogin();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
		
		forget.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginDetailActivity.this,ForgetActivity.class);
				startActivity(intent);
				finish();
				onDestroy();
			}});
		if (checkState) {
			mRemember.setChecked(true);
			phone.setText(phone_l); 
        	password.setText(pw_l);
        	
        }else{
        	phone.setText(null); 
        	password.setText(null);
        }

		
//		mRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
//            @Override 
//            public void onCheckedChanged(CompoundButton buttonView, 
//                    boolean isChecked) { 
//                // TODO Auto-generated method stub 
//                if(isChecked){ 
//                	phone.setText(phone_l); 
//                	password.setText(pw_l);
//                }else{ 
//                	phone.setText(null); 
//                	password.setText(null);
//                } 
//            } 
//        }); 
	}
	
	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 4;
	}

	public void showProgress(final boolean show) {
		mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);		
	}
	
	public void attemptLogin() throws JSONException {
		RequestParams rp = new RequestParams();
		final String mPhone = phone.getText().toString();
		final String mPassword = password.getText().toString();
		boolean cancel = false;
		View focusView = null;
		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(mPassword) || !isPasswordValid(mPassword)) {
			
			password.setError(Html.fromHtml("<font color=#E10979>密码太短，应大于4位</font>"));
			focusView = password;
			cancel = true;
			
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			showProgress(false);
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
						
			JSONObject tmp = new JSONObject();
			tmp.put("phone", mPhone);
			tmp.put("password", mPassword);
						
			Log.d("nihao",tmp.toString());
			rp.put("data", tmp.toString());
			JsonHttpResponseHandler jrh = new JsonHttpResponseHandler("UTF-8") {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String response) {
					//Toast.makeText(LoginDetailActivity.this, response.toString(), Toast.LENGTH_LONG).show();
					int state_num =4;
				 	for(int i=0;i<headers.length;i++){
					 	Log.d("nihao",headers[i].toString());
					 	if(isInteger(headers[i].getValue().toString())){
					 		state_num = Integer.parseInt(headers[i].getValue());
					 		Log.d("nihao",headers[i].getValue());
					 	}
					 	}
				 	if(state_num ==0){
					Intent intent = new Intent();
					intent.setClass(LoginDetailActivity.this,MainActivity.class);
					editor.putString("phone", mPhone);
					editor.putString("password", mPassword);
					editor.putString("usr_name", response);
					editor.putBoolean("remember", mRemember.isChecked());
					
					editor.commit();
					Toast.makeText(LoginDetailActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
					showProgress(false);
					startActivity(intent);
					finish();
					onDestroy();
					}else {
						//Toast.makeText(LoginDetailActivity.this, "账号或用户名错误", Toast.LENGTH_LONG).show();
						password.setError(Html.fromHtml("<font color=#E10979>手机号或者密码错误</font>"));
						phone.setError(Html.fromHtml("<font color=#E10979>手机号或者密码错误</font>"));
						showProgress(false);
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseBody, Throwable e) {
					Toast.makeText(LoginDetailActivity.this, statusCode + "\t" + responseBody, Toast.LENGTH_LONG).show();
					showProgress(false);
				}
							
			};
			httpHelper.post(BASE_URL+"/login",rp, jrh);}
			
			
			
		}
	
	public static boolean isInteger(String input){  
    Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);  
    return mer.find();  
}  
	
	
	
}
