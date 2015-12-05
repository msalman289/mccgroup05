package com.example.theodosis.calendar365;
/**
 * Created by Salman Ishaq on 11/20/2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UpcomingEvnt extends Fragment {
    SharedPreferences pref, editpref;
    private ArrayList<Event> eventsList = new ArrayList<>();
    Event event365;
    List<NameValuePair> params, params1;
    final List<String[]> eventList = new LinkedList<String[]>();;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.upcoming_event,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        editpref = this.getActivity().getSharedPreferences("EditEventPref", Context.MODE_PRIVATE);

        String eventsstring = pref.getString("events", null);

        JSONArray eventsListJSON;
        try {
            if(eventsstring != null) {
                eventsListJSON = new JSONArray(eventsstring);
                for (int i = 0; i < eventsListJSON.length(); i++) {

                    JSONObject pdtObj = eventsListJSON.getJSONObject(i);
                    event365 = new Event(pdtObj.getString("_id"),pdtObj.getString("description"),pdtObj.getString("place"),
                            pdtObj.getString("date"),pdtObj.getString("starttime"), pdtObj.getString("endtime"));

                    event365.set_id(pdtObj.getString("_id"));
                    event365.setDescription(pdtObj.getString("description"));
                    event365.setplace(pdtObj.getString("place"));
                    event365.setDate(pdtObj.getString("date"));
                    event365.setStartTime(pdtObj.getString("starttime"));
                    event365.setEndTime(pdtObj.getString("endtime"));
                    eventsList.add(event365);
                    eventList.add(new String[] { pdtObj.getString("date"), pdtObj.getString("description")});
                }
            }
        } catch (JSONException e) {
            return;
        }

        ListView mListView = (ListView)getActivity().findViewById(R.id.upcomevents365);
        mListView.setAdapter(new ArrayAdapter<String[]>(getActivity(),android.R.layout.simple_list_item_2,android.R.id.text1,eventList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] entry = eventList.get(position);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(entry[0]);
                text2.setText(entry[1]);
                return view;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                LayoutInflater li = getActivity().getLayoutInflater();
                View promptsView = li.inflate(R.layout.show_event_prompt, null);

                final Event eventinformation = eventsList.get(arg2);
                // eventinformation in shared preference so that it can be seen on the editevent page.
                SharedPreferences.Editor editEvent = editpref.edit();
                editEvent.putString("editeventinformation", eventinformation.toJSON().toString());
                editEvent.commit();

                TextView datetext = (TextView)promptsView.findViewById(R.id.date);
                datetext.setText(eventinformation.getDate());

                TextView starttimetext = (TextView)promptsView.findViewById(R.id.starttime);
                starttimetext.setText(eventinformation.getStartTime());

                TextView endtimetext = (TextView)promptsView.findViewById(R.id.endtime);
                endtimetext.setText(eventinformation.getEndTime());

                TextView desctext = (TextView)promptsView.findViewById(R.id.description);
                desctext.setText(eventinformation.getDescription());

                TextView placetext = (TextView)promptsView.findViewById(R.id.place);
                placetext.setText(eventinformation.getplace());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Event Details:");
                alertDialogBuilder.setView(promptsView);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Edit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(getActivity(), EditEvnt.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNeutralButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        params = new ArrayList<NameValuePair>();
                                        String eventid = eventinformation.get_id().toString();
                                        ServerRequestDelete sr = new ServerRequestDelete();
                                        JSONObject json = sr.getJSON("http://130.233.42.186:8080/deleteevent/"+eventid, params);
                                        if (json != null) {
                                            try{
                                                if(json.length() > 0) {
                                                    String message = "Event is Deleted Successfully!!";
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
                                                        else {
                                                            edit.putString("events", "");
                                                            edit.commit();
                                                        }
                                                    }

                                                    Intent backToCalView = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(backToCalView);
                                                    getActivity().finish();
                                                }
                                                else {
                                                    String message = "Error while Deleting event!!";
                                                    Toast.makeText(getActivity().getApplication(), message, Toast.LENGTH_LONG).show();
                                                    Log.d("Alert", message);
                                                }
                                            }catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                })
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
        });
    }
}
