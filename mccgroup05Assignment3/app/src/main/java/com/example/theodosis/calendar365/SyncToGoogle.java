package com.example.theodosis.calendar365;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;

/**
 * Created by Salman Ishaq on 28/11/2015.
 */
public class SyncToGoogle extends Fragment {

    SharedPreferences pref, editpref;
    private ArrayList<Event> eventsList = new ArrayList<>();
    Event event365;
    List<NameValuePair> params, params1;
    final List<String[]> eventList = new LinkedList<String[]>();;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sync_to_google, container, false);
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
        final ListView importListView = (ListView) getActivity().findViewById(R.id.synctoevents365);
        importListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] calendarList = new String[1];
        loop:
        for (me.everything.providers.android.calendar.Calendar c : calendars) {
            calendarList[0] = c.displayName;
            break loop;
        }
        ArrayAdapter<String> calendarArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, calendarList);
        importListView.setAdapter(calendarArrayAdapter);

        // Pick a calendar and show events
        importListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                // Display Events here like upcoming events
                pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                editpref = getActivity().getSharedPreferences("EditEventPref", Context.MODE_PRIVATE);

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

                ListView mListView = (ListView)getActivity().findViewById(R.id.synctoevents365);
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
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        final Event eventinformation = eventsList.get(arg2);
                        Intent intent = new Intent(Intent.ACTION_EDIT); // ACTION_INSERT
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE, eventinformation.getDescription());
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventinformation.getplace());
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, eventinformation.getDescription());
                        intent.putExtra(CalendarContract.Events.CALENDAR_TIME_ZONE, "Europe/Helsinki");
                        String expectedPattern = "yyyy-MM-dd HH:mm";
                        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
                        String statttime365 = eventinformation.getDate() + " " + eventinformation.getStartTime();//"2016-12-12 12:30";
                        String endtime365 = eventinformation.getDate() + " " + eventinformation.getEndTime();    //"2016-12-12 15:30";
                        try {
                            Date date = formatter.parse(statttime365);
                            Date date1 = formatter.parse(endtime365);
                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,date.getTime());
                            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,date1.getTime());
                        } catch (ParseException e) {return;}
                        startActivity(intent);
                        String message = "Add your calendar 365 events into Mobile Calendar!!!";
                        Toast.makeText(getActivity().getApplication(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
