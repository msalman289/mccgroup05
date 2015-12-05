package com.example.theodosis.calendar365;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View.OnClickListener;


/**
 * Created by Theodosis on 11/23/2015.
 */
public class AddEvnt extends Activity implements OnClickListener {

    EditText starttime,endtime,description,location;
    Button addevent, discardevent;
    String datetxt,starttimetxt,endtimetxt,descriptiontxt,locationtxt;
    List<NameValuePair> params, params1;
    SharedPreferences pref;

    EditText dateEtxt;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event365);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById();
        setDateTimeField();

        starttime = (EditText) findViewById(R.id.starttime);
        endtime = (EditText) findViewById(R.id.endtime);
        description = (EditText) findViewById(R.id.description);
        location = (EditText) findViewById(R.id.location);

        addevent = (Button) findViewById(R.id.addbtn);
        discardevent = (Button) findViewById(R.id.discardbtn);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        addevent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dateEtxt.setOnClickListener(this);
                starttimetxt = starttime.getText().toString();
                endtimetxt = endtime.getText().toString();
                descriptiontxt = description.getText().toString();
                locationtxt = location.getText().toString();

                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("place", locationtxt));
                params.add(new BasicNameValuePair("description", descriptiontxt));
                params.add(new BasicNameValuePair("date", dateEtxt.getText().toString()));
                params.add(new BasicNameValuePair("starttime", starttimetxt));
                params.add(new BasicNameValuePair("endtime", endtimetxt));
                params.add(new BasicNameValuePair("userid", pref.getString("userid",null)));

                ServerRequestPost sr = new ServerRequestPost();
                JSONObject json = sr.getJSON("http://130.233.42.186:8080/adduserevent", params);

                if (json != null) {
                    try{
                        if(json.length() > 0) {
                            String message = "Event is created Successfully!!";
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            Log.d("Alert", message);

                            //  send the GET call to server to get refreshed events from DB.
                            SharedPreferences.Editor edit = pref.edit();
                            String userid = pref.getString("userid", null);
                            params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", userid));
                            ServerRequestGet sr1 = new ServerRequestGet();
                            JSONObject json1 = sr1.getJSON("http://130.233.42.186:8080/eventlist", params1);
                            if (json1 != null) {
                                if(json1.getJSONArray("events").length() > 0) {
                                    String events = json1.getJSONArray("events").toString();
                                    edit.putString("events", events);
                                    edit.commit();
                                }
                            }
                            Intent backToCalView = new Intent(AddEvnt.this, LoginActivity.class);
                            startActivity(backToCalView);
                            finish();
                        }
                        else {
                            String message = "Error while creating event!!";
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            Log.d("Alert", message);
                            Intent addevent = new Intent(AddEvnt.this, LoginActivity.class);
                            startActivity(addevent);
                            finish();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        discardevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent disc = new Intent(AddEvnt.this, LoginActivity.class);
                startActivity(disc);
                finish();
            }
        });
    }

    private void findViewsById() {
        dateEtxt = (EditText) findViewById(R.id.etxt_date);
        dateEtxt.setInputType(InputType.TYPE_NULL);
        dateEtxt.requestFocus();
    }

    private void setDateTimeField() {
        dateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {
        if(view == dateEtxt) {
            datePickerDialog.show();
        }
    }

}