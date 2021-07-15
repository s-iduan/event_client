package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.model.Constants;
import com.example.event_cord.model.Event;
import com.example.event_cord.model.UserEventPair;
import com.example.event_cord.utility.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {
    private Button mCreateButton;
    private EditText mTitle;
    private EditText mDescription;

    private int mEventId;
    private String mEventTitle;
    private String mEventDescription;
    private boolean mIsEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mEventId = bundle.getInt(Constants.EVENT_ID_KEY, 0);
            mEventTitle = bundle.getString(Constants.EVENT_TITLE_KEY, "");
            mEventDescription = bundle.getString(Constants.EVENT_DESCRIPTION_KEY, "");
            mIsEditing = bundle.getBoolean(Constants.EVENT_IS_EDITING_KEY, false);
        }

        mCreateButton = findViewById(R.id.createEvent);
        mTitle = findViewById(R.id.EditTextTitle);
        mDescription = findViewById(R.id.edittext_description);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEditing) {
                    createEvent();
                } else {
                    updateEvent();
                }
            }
        });

        if (mIsEditing) {
            mTitle.setText(mEventTitle);
            mDescription.setText(mEventDescription);
            mCreateButton.setText("Update Event");
        }
    }

    private void updateEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        int userId = Helper.getLoggedinUserId(this);

        Event event = new Event(mEventId, userId, title, description);
        GetDataService service = RestClient.getRetrofit().create((GetDataService.class));
        Call<Void> call = service.updateEvent(event);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Intent intent = new Intent(CreateEventActivity.this, EventsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        int userId = Helper.getLoggedinUserId(this);

        Event event = new Event(-1, userId, title, description);
        GetDataService service = RestClient.getRetrofit().create(GetDataService.class);
        Call<UserEventPair> call = service.createEvent(event);

        call.enqueue(new Callback<UserEventPair>() {
            @Override
            public void onResponse(Call<UserEventPair> call, Response<UserEventPair> response) {
                Intent intent = new Intent(CreateEventActivity.this, EventsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<UserEventPair> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}