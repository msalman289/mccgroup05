package com.example.theodosis.calendar365;

/**
 * Created by Salman Ishaq on 21/11/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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



public class RegisterActivity extends Activity {
    EditText email,password,firstName,lastName,username;
    Button login,register;
    String emailtxt,passwordtxt,firstnametxt,lastnametxt,usernametxt;
    List<NameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        username = (EditText)findViewById(R.id.username);
        register = (Button)findViewById(R.id.registerbtn);
        //login = (Button)findViewById(R.id.login);

        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regactivity = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(regactivity);
                finish();
            }
        });*/


        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();
                firstnametxt = firstName.getText().toString();
                lastnametxt = lastName.getText().toString();
                usernametxt = username.getText().toString();

                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailtxt));
                params.add(new BasicNameValuePair("password", passwordtxt));
                params.add(new BasicNameValuePair("username", usernametxt));
                params.add(new BasicNameValuePair("firstName", firstnametxt));
                params.add(new BasicNameValuePair("lastName", lastnametxt));

                ServerRequestPost sr = new ServerRequestPost();
                JSONObject json = sr.getJSON("http://130.233.42.186:8080/signup",params);

                if(json != null){
                    try{
                        Boolean errorMessage = json.getBoolean("error_message");
                        if(errorMessage == true) {
                            String message = json.getString("message");
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            Log.d("Alert", message);
                        }
                        else {
                            //RegisterActivity.this.setContentView(R.layout.activity_main);
                            Intent loginactivity = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(loginactivity);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




}