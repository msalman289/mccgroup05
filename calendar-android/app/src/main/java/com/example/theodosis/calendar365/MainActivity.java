package com.example.theodosis.calendar365;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity  {
    EditText username,password;
    Button login,register;
    String usernametxt,passwordtxt;
    List<NameValuePair> params;
    SharedPreferences pref;
    ServerRequestPost sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //display the logo during 1-2 seconds,
        new CountDownTimer(1100,1000){
            @Override
            public void onTick(long millisUntilFinished){}
            @Override
            public void onFinish(){
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.login);

                sr = new ServerRequestPost();

                username = (EditText)findViewById(R.id.username);
                password = (EditText)findViewById(R.id.password);
                login = (Button)findViewById(R.id.loginbtn);
                register = (Button)findViewById(R.id.register);

                pref = getSharedPreferences("AppPref", MODE_PRIVATE);

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent regactivity = new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(regactivity);
                    }
                });


                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        usernametxt = username.getText().toString();
                        passwordtxt = password.getText().toString();
                        params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("username", usernametxt));
                        params.add(new BasicNameValuePair("password", passwordtxt));
                        ServerRequestPost sr = new ServerRequestPost();
                        JSONObject json = sr.getJSON("http://130.233.42.186:8080/login", params);
                        if (json != null) {
                            try {
                                Boolean errorMessage = json.getBoolean("error_message");
                                if (errorMessage == false && json.getJSONObject("user").length() >0) {
                                    SharedPreferences.Editor edit = pref.edit();
                                    //Storing Data using SharedPreferences
                                    edit.putString("userid","");
                                    String userid = json.getJSONObject("user").getString("_id");
                                    edit.putString("userid",userid);
                                    edit.putString("user", "");
                                    String userInfo = json.getJSONObject("user").toString();
                                    edit.putString("user", userInfo);
                                    edit.putString("events", "");
                                    if(json.getJSONArray("events").length() > 0) {
                                        String eventsInfo = json.getJSONArray("events").toString();
                                        edit.putString("events", eventsInfo);
                                    }
                                    edit.commit();
                                    Intent loginactivity = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(loginactivity);
                                }
                                else {
                                    String message = json.getString("message");
                                    Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();
    }
}
