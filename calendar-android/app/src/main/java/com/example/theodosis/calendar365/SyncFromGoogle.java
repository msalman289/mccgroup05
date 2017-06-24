package com.example.theodosis.calendar365;

/**
 * Created by Theodosis on 11/19/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncFromGoogle extends Fragment {
    String ED_endMinutes, ED_endHours, SD_startMinutes, SD_startHours;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    List<NameValuePair> params, params1;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sync_from_google, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Calendar providers
        final CalendarProvider calendarProvider = new CalendarProvider(getContext());
        final List<Calendar> calendars = calendarProvider.getCalendars().getList();
        // View
        final Intent intent = getActivity().getIntent();
        final ListView importListView = (ListView) getActivity().findViewById(R.id.syncevents365);
        importListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] calendarList = new String[1];
        loop:
        for (me.everything.providers.android.calendar.Calendar c : calendars) {
            calendarList[0] = c.displayName;
            break loop;
        }
        ArrayAdapter<String> calendarArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, calendarList);
        importListView.setAdapter(calendarArrayAdapter);
        pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);


        // Pick a calendar and show events
        importListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                final Long calendar_ID = calendars.get(pos).id;
                final Spanned[] eventList = new Spanned[calendarProvider.getEvents(calendar_ID).getList().size()];
                int i = 0;
                for (me.everything.providers.android.calendar.Event e : calendarProvider.getEvents(calendar_ID).getList()) {
                    eventList[i] = Html.fromHtml("<b>" + e.title + "</b><br/>");
                    i++;
                }
                ArrayAdapter<Spanned> eventsArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventList);
                importListView.setAdapter(eventsArrayAdapter);

                importListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos,
                                            long id) {

                        me.everything.providers.android.calendar.Event selected_event = calendarProvider.getEvents(calendar_ID).getList().get(pos);

                        Date currentDate = new Date(selected_event.dTStart);
                        Date endDate = new Date(selected_event.dTend);
                        String date = df.format(currentDate).toString();
                        String description = selected_event.title.toString();
                        String place = selected_event.eventLocation.toString();
                        if(endDate.getMinutes() < 9) {
                            ED_endMinutes = "0" + endDate.getMinutes();
                        } else {
                            ED_endMinutes = Integer.toString(endDate.getMinutes());
                        }
                        if(endDate.getHours() < 9) {
                            ED_endHours = "0" + endDate.getHours();
                        } else {
                            ED_endHours = Integer.toString(endDate.getHours());
                        }
                        if(currentDate.getMinutes() < 9) {
                            SD_startMinutes = "0" + currentDate.getMinutes();
                        } else {
                            SD_startMinutes = Integer.toString(currentDate.getMinutes());
                        }
                        if(currentDate.getHours() < 9) {
                            SD_startHours = "0" + currentDate.getHours();
                        } else {
                            SD_startHours = Integer.toString(currentDate.getHours());
                        }
                        String starttime = SD_startHours + ":" + SD_startMinutes;
                        String endtime = ED_endHours + ":" + ED_endMinutes;

                        // declare params and add values to it and call addevent
                        params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("place", place));
                        params.add(new BasicNameValuePair("description", description));
                        params.add(new BasicNameValuePair("date", date));
                        params.add(new BasicNameValuePair("starttime", starttime));
                        params.add(new BasicNameValuePair("endtime", endtime));
                        params.add(new BasicNameValuePair("userid", pref.getString("userid",null)));

                        ServerRequestPost sr = new ServerRequestPost();
                        JSONObject json = sr.getJSON("http://130.233.42.186:8080/adduserevent", params);
                        if (json != null) {
                            try{
                                if(json.length() > 0) {
                                    String message = "Selected mobile calendar event is added to Calendar 365!!";
                                    Toast.makeText(getActivity().getApplication(), message, Toast.LENGTH_LONG).show();
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
                                    Intent backToCalView = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(backToCalView);
                                    getActivity().finish();
                                }
                                else {
                                    String message = "Error while adding selected mobile calendar event to Calendar 365!!";
                                    Toast.makeText(getActivity().getApplication(), message, Toast.LENGTH_LONG).show();
                                    Log.d("Alert", message);
                                    Intent addevent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(addevent);
                                    getActivity().finish();
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

}