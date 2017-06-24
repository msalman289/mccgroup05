package com.example.theodosis.calendar365;

/**
 * Created by Theodosis on 11/19/2015.
 */
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarView extends Fragment {
    private FragmentActivity myContext;
    private ArrayList<Event> eventsList = new ArrayList<>();
    Button addeventbtn;
    SharedPreferences pref;

    private Date selectedDate;

    final CaldroidFragment caldroidFragment = new CaldroidFragment();

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar365_view,container,false);
        addeventbtn = (Button) v.findViewById(R.id.addeventbtncal365);

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction t = fragManager.beginTransaction();
        t.replace(R.id.cal365, caldroidFragment);
        t.commit();
        pref = this.getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        // Setup Events
        setupEvents();

        addeventbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEvnt.class);
                startActivity(intent);
            }
        });


        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate = date;
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date , View view) {
                selectedDate = date;
                Intent intent = new Intent(getActivity(), AddEvnt.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // additional line ????
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {

                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);


        return v;
    }


    private void setupEvents() {
        // Fetch already existing events from Shared Preferences
        String eventsstring = pref.getString("events", null);
        JSONArray eventsListJSON;
        try {
            if(eventsstring != null) {
                eventsListJSON = new JSONArray(eventsstring);
                for (int i = 0; i < eventsListJSON.length(); i++) {
                    JSONObject pdtObj = eventsListJSON.getJSONObject(i);
                    Event event365 = new Event(pdtObj.getString("_id"),pdtObj.getString("description"),pdtObj.getString("place"),
                            pdtObj.getString("date"),pdtObj.getString("starttime"), pdtObj.getString("endtime"));

                    event365.set_id(pdtObj.getString("_id"));
                    event365.setDescription(pdtObj.getString("description"));
                    event365.setplace(pdtObj.getString("place"));
                    event365.setDate(pdtObj.getString("date"));
                    event365.setStartTime(pdtObj.getString("starttime"));
                    event365.setEndTime(pdtObj.getString("endtime"));

                    eventsList.add(event365);
                }
            }
        } catch (JSONException e) {
        }



        showEvents();
    }

    private void showEvents() {
        // Add event to calendar
        for (Event e : eventsList) {
            for (Date d : getDatesRange(e))
                caldroidFragment.setBackgroundResourceForDate(R.color.blue, d);
            Log.d("Cal365", "Event " + e.toString() + " added to calendar.");
        }
        // Refresh calendar view
        caldroidFragment.refreshView();
    }

    private ArrayList<Date> getDatesRange(Event e) {
        ArrayList<Date> dates = new ArrayList<>();
        Calendar c_dateStart = Calendar.getInstance();
        Calendar c_dateEnd = Calendar.getInstance();
        c_dateStart.setTime(convertStringToDate(e.getDate()));
        c_dateEnd.setTime(convertStringToDate(e.getDate()));
        dates.add(new Date(c_dateStart.getTimeInMillis()));
        return dates;
    }

    public Date convertStringToDate(String dateString) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(dateString);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}