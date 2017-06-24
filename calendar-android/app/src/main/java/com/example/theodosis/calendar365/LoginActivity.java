package com.example.theodosis.calendar365;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Salman Ishaq on 21/11/2015.
 */
public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Calendar View","Upcoming Events","Sync From Google", "Sync To Google"};
    int Numboftabs =4;
    SharedPreferences pref,eventpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_user) {
            showuserInfoDialog();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.action_logout) {
            try {
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("user", "");
                edit.putString("userid", "");
                edit.putString("events", "");
                edit.commit();

                eventpref = getSharedPreferences("EditEventPref", MODE_PRIVATE);
                SharedPreferences.Editor editevent = eventpref.edit();
                editevent.putString("editeventinformation", "");
                editevent.commit();

                Intent loginactivity = new Intent(LoginActivity.this, MainActivity.class);
                loginactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginactivity);
                finish();
            } catch(Exception e) {
                Intent loginactivity = new Intent(LoginActivity.this, MainActivity.class);
                loginactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginactivity);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.about_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LoginActivity.this);
        // set xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    private void showuserInfoDialog() {
        String lastname = "";String firstname = "";String email = "";
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        String jsonstring = pref.getString("user", null);
        JSONObject jsonobj = null;
        try {
            if(jsonstring != null) {
                jsonobj = new JSONObject(jsonstring);
                lastname = jsonobj.get("lastName").toString();
                firstname = jsonobj.get("firstName").toString();
                email =  jsonobj.get("email").toString();
            }
        } catch (JSONException e) {
        }
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);

        View promptsView = li.inflate(R.layout.userinfo_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setTitle("User Information:");
        // set xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        TextView fntext = (TextView)promptsView.findViewById(R.id.firstname);
        fntext.setText(firstname);
        TextView lntext = (TextView)promptsView.findViewById(R.id.lastname);
        lntext.setText(lastname);
        TextView emtext = (TextView)promptsView.findViewById(R.id.email1);
        emtext.setText(email);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
