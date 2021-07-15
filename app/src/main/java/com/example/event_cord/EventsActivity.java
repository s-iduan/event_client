package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.adapter.EventAdapter;
import com.example.event_cord.adapter.OnEventClickListener;
import com.example.event_cord.adapter.OnEventDeletionHandler;
import com.example.event_cord.adapter.OnEventEditHandler;
import com.example.event_cord.model.Constants;
import com.example.event_cord.model.Event;
import com.example.event_cord.utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsActivity extends AppCompatActivity {

    private static String TAG = "EventsActivity";

    private boolean showOnlyMyEvents = false;

    private EventAdapter adapter;
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private Button showAllEventButton;
    private Button showOnlyMyEventButton;
    private FloatingActionButton mFloatingActionButton;

    OnEventClickListener mOnEventClickLister = new OnEventClickListener() {
        @Override
        public void onItemClicked(Event item) {
            Toast.makeText(EventsActivity.this, item.getName() + " " + item.getDescription(), Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.EVENT_ID_KEY, item.getId());
            bundle.putString(Constants.EVENT_TITLE_KEY, item.getName());
            bundle.putString(Constants.EVENT_DESCRIPTION_KEY, item.getDescription());
            bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, item.getLastModifytime());
            bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, item.getUserId());

            Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    OnEventEditHandler mOnEventEditHandler = new OnEventEditHandler() {
        @Override
        public void editEvent(Event item) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.EVENT_ID_KEY, item.getId());
            bundle.putString(Constants.EVENT_TITLE_KEY, item.getName());
            bundle.putString(Constants.EVENT_DESCRIPTION_KEY, item.getDescription());
            bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, item.getLastModifytime());
            bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, item.getUserId());
            bundle.putBoolean(Constants.EVENT_IS_EDITING_KEY, true);

            Intent intent = new Intent(EventsActivity.this, CreateEventActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    OnEventDeletionHandler mEventDeletionHandler = new OnEventDeletionHandler() {
        @Override
        public void deleteEvent(int eventId) {
            Event event = new Event(eventId, 0, "","", 0, 0);

            GetDataService service = RestClient.getRetrofit().create(GetDataService.class);
            Call<Void> call = service.deleteEvent(eventId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    updateEventList();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        progressDialog = new ProgressDialog(EventsActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        showAllEventButton = findViewById(R.id.show_all_events);
        showOnlyMyEventButton = findViewById(R.id.show_my_events);
        mFloatingActionButton = findViewById(R.id.fabAddButton);

        showAllEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnlyMyEvents = false;
                updateButtonStatus();
                updateEventList();
            }
        });

        showOnlyMyEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnlyMyEvents = true;
                updateButtonStatus();
                updateEventList();
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        updateEventList();
        updateButtonStatus();
    }

    private void updateButtonStatus() {
        if (showOnlyMyEvents) {
            showAllEventButton.setVisibility(View.VISIBLE);
            showOnlyMyEventButton.setVisibility(View.GONE);
        } else {
            showAllEventButton.setVisibility(View.GONE);
            showOnlyMyEventButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateEventList() {
        GetDataService service = RestClient.getRetrofit().create(GetDataService.class);
        int userId = Helper.getLoggedinUserId(this);
        if (userId < 0) {
            Log.e(TAG, "wrong user id returned, ask user to log in again");
            Intent intent = new Intent(this, UserLogin.class);
            startActivity(intent);
            return;
        }

        Call<List<Event>> call = service.getAllEvents();
        if (showOnlyMyEvents) {
            call = service.getMyEvents(userId);
        }
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, t.getLocalizedMessage());
                t.printStackTrace();
                Toast.makeText(EventsActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateDataList(List<Event> eventList) {
        recyclerView = findViewById(R.id.eventRecyclerView);
        adapter = new EventAdapter(eventList, mOnEventClickLister, mEventDeletionHandler, mOnEventEditHandler);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(EventsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}