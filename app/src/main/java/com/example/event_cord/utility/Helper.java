package com.example.event_cord.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.event_cord.EventCalendarActivity;
import com.example.event_cord.EventsActivity;
import com.example.event_cord.model.Constants;

public class Helper {

    public static int getLoggedinUserId (Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PATH_LOGGED_IN_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.USER_ID)) {
            return sharedPreferences.getInt(Constants.USER_ID, -1);
        }
        return -1;
    }

    public static void navigateToCalendarView(Context context) {
        Intent intent = new Intent(context, EventCalendarActivity.class);
        context.startActivity(intent);
    }

    public static void navigateToListView(Context context) {
        Intent intent = new Intent(context, EventsActivity.class);
        context.startActivity(intent);
    }
}
