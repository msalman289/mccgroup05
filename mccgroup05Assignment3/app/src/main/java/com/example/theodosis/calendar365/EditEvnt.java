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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Salman Ishaq on 11/23/2015.
 */
public class EditEvnt extends Activity implements OnClickListener {

    TextView starttime,endtime,description,location;
    Button editvent, discardEditevent;
    String datetxt,starttimetxt,endtimetxt,descriptiontxt,locationtxt, eventid;
    List<NameValuePair> params, params1;
    SharedPreferences pref, editPref;
    JSONObject jsoneditevent;
    TextView dateEtxt;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event365);

        editPref = getSharedPreferences("EditEventPref", MODE_PRIVATE);
        String editEvent = editPref.getString("editeventinformation", null);
        try {
            jsoneditevent = new JSONObject(editEvent);
            datetxt = jsoneditevent.get("date").toString();
            starttimetxt = jsoneditevent.get("starttime").toString();
            endtimetxt = jsoneditevent.get("endtime").toString();
            descriptiontxt = jsoneditevent.get("description").toString();
            locationtxt = jsoneditevent.get("place").toString();

            dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            findViewsById();
            setDateTimeField();

            starttime = (TextView) findViewById(R.id.editstarttime);
            starttime.setText(starttimetxt);
            endtime = (TextView) findViewById(R.id.editendtime);
            endtime.setText(endtimetxt);
            description = (TextView) findViewById(R.id.editdescription);
            description.setText(descriptiontxt);
            location = (TextView) findViewById(R.id.editlocation);
            location.setText(locationtxt);

        } catch (JSONException e) {
            Log.d("TAG:","Error");
        }

        editvent = (Button) findViewById(R.id.editbtn);
        discardEditevent = (Button) findViewById(R.id.discardEditbtn);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        editvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dateEtxt.setOnClickListener(this);
                starttime = (TextView) findViewById(R.id.editstarttime);
                endtime = (TextView) findViewById(R.id.editendtime);
                description = (TextView) findViewById(R.id.editdescription);
                location = (TextView) findViewById(R.id.editlocation);

                starttimetxt = starttime.getText().toString();
                endtimetxt = endtime.getText().toString();
                descriptiontxt = description.getText().toString();
                locationtxt = location.getText().toString();
                datetxt = dateEtxt.getText().toString();

                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("place", locationtxt));
                params.add(new BasicNameValuePair("description", descriptiontxt));
                params.add(new BasicNameValuePair("date", datetxt));
                params.add(new BasicNameValuePair("starttime", starttimetxt));
                params.add(new BasicNameValuePair("endtime", endtimetxt));

                ServerRequestPut sr = new ServerRequestPut();
                try {
                    eventid = jsoneditevent.get("_id").toString();
                } catch (JSONException e) {
                    return;
                }
                JSONObject json = sr.getJSON("http://130.233.42.186:8080/save_editevent/"+eventid, params);

                if (json != null) {
                    try{
                        if(json.length() > 0) {
                            String message = "Event is Edited Successfully!!";
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
                            Intent backToCalView = new Intent(EditEvnt.this, LoginActivity.class);
                            startActivity(backToCalView);
                            finish();
                        }
                        else {
                            String message = "Error while Editing event!!";
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            Log.d("Alert", message);
                            Intent addevent = new Intent(EditEvnt.this, LoginActivity.class);
                            startActivity(addevent);
                            finish();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        discardEditevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent disc = new Intent(EditEvnt.this, LoginActivity.class);
                startActivity(disc);
                finish();
            }
        });
    }

    private void findViewsById() {
        dateEtxt = (TextView) findViewById(R.id.editetxt_date);
        dateEtxt.setText(datetxt);
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