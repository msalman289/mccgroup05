package com.example.theodosis.calendar365;

/**
 * Created by Salman Ishaq on 23/11/2015.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Event {

    private String _id, description, place;
    private String date;
    private String starttime, endtime;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return _id.equals(event._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public Event(String _id, String description, String place, String date, String starttime, String endtime) {
        this._id = _id;
        this.date = date;
        this.description = description;
        this.place = place;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    // Used for dirty search
    public Event(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id='" + _id + '\'' +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", date='" + date + '\'' +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("_id",_id);
            jo.put("description",description);
            jo.put("place",place);
            jo.put("date",date);
            jo.put("starttime",starttime);
            jo.put("endtime",endtime);

        } catch (JSONException e) {
            jo = null;
        }
        return jo;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setplace(String place) {
        this.place = place;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String starttime) {
        this.starttime = starttime;
    }

    public void setEndTime(String endtime) {
        this.endtime = endtime;
    }

    public String get_id() {

        return _id;
    }

    public String getDescription() {
        return description;
    }

    public String getplace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return starttime;
    }

    public String getEndTime() {
        return endtime;
    }
}
