package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.fragment.DatePickerDialogFragment;
import com.example.event_cord.fragment.TimePickerDialogFragment;
import com.example.event_cord.model.Constants;
import com.example.event_cord.model.Event;
import com.example.event_cord.model.UserEventPair;
import com.example.event_cord.utility.Helper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {
    private Button mCreateButton;
    private EditText mTitle;
    private EditText mDescription;

    private EditText mStartDate;
    private EditText mStartTime;
    private EditText mEndDate;
    private EditText mEndTime;

    private int mEventId;
    private String mEventTitle;
    private String mEventDescription;
    private boolean mIsEditing;

    private long mStartDateLong;
    private long mStartTimeLong;
    private long mEndDateLong;
    private long mEndTimeLong;

    private DatePickerDialog.OnDateSetListener OnStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar mCalender = Calendar.getInstance();
            mCalender.set(Calendar.YEAR, year);
            mCalender.set(Calendar.MONTH, month);
            mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
            mStartDate.setText(selectedDate);
            mStartDateLong = mCalender.getTime().getTime();

            if (mEndDate.getText().toString().isEmpty()) {
                mEndDate.setText(selectedDate);
                mEndDateLong = mCalender.getTime().getTime();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener OnEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar mCalender = Calendar.getInstance();
            mCalender.set(Calendar.YEAR, year);
            mCalender.set(Calendar.MONTH, month);
            mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
            mEndDate.setText(selectedDate);
            mEndDateLong = mCalender.getTime().getTime();

            if (mStartDate.getText().toString().isEmpty()) {
                mStartDate.setText(selectedDate);
                mStartDateLong = mCalender.getTime().getTime();
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateTimeField(mStartTime, hourOfDay, minute);
            mStartTimeLong = (hourOfDay * 60 + minute) * 1000;
        }
    };

    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateTimeField(mEndTime, hourOfDay, minute);
            mEndTimeLong = (hourOfDay * 60 + minute) * 1000;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mCreateButton = findViewById(R.id.createEvent);
        mTitle = findViewById(R.id.EditTextTitle);
        mDescription = findViewById(R.id.edittext_description);
        mStartDate = findViewById(R.id.edittextStartDate);
        mStartTime = findViewById(R.id.edittextStartTime);
        mEndDate = findViewById(R.id.edittextEndDate);
        mEndTime = findViewById(R.id.edittextEndTime);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mEventId = bundle.getInt(Constants.EVENT_ID_KEY, 0);
            mEventTitle = bundle.getString(Constants.EVENT_TITLE_KEY, "");
            mEventDescription = bundle.getString(Constants.EVENT_DESCRIPTION_KEY, "");
            mIsEditing = bundle.getBoolean(Constants.EVENT_IS_EDITING_KEY, false);

            long startTime = bundle.getLong(Constants.EVENT_START_TIME);
            updateStartDateTime(startTime);
            long endTime = bundle.getLong(Constants.EVENT_END_TIME);
            updateEndDateTime(endTime);
        }

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

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePicker = new DatePickerDialogFragment(OnStartDateSetListener);
                datePicker.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePicker = new DatePickerDialogFragment(OnEndDateSetListener);
                datePicker.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment timePicker = new TimePickerDialogFragment(mStartTimeSetListener);
                timePicker.show(getSupportFragmentManager(), "TIME PICK");
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment timePicker = new TimePickerDialogFragment(mEndTimeSetListener);
                timePicker.show(getSupportFragmentManager(), "TIME PICK");
            }
        });
    }

    private void updateEvent() {
        if (!validateInput()) {
            return;
        }
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        int userId = Helper.getLoggedinUserId(this);

        Event event = new Event(mEventId, userId, title, description, mStartDateLong+ mStartTimeLong, mEndDateLong + mEndTimeLong);
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
        if (!validateInput()) {
            return;
        }
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        int userId = Helper.getLoggedinUserId(this);

        Event event = new Event(-1, userId, title, description, mStartTimeLong+ mStartDateLong, mEndDateLong + mEndTimeLong);
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

    private boolean validateInput() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String startDate = mStartDate.getText().toString();
        String startTime = mStartTime.getText().toString();
        String endTime = mEndTime.getText().toString();
        String endDate = mEndDate.getText().toString();

        if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() || startDate.isEmpty() || startTime.isEmpty() || endDate.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(CreateEventActivity.this, "Some field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateStartDateTime(long time) {
        Calendar mCalender = Calendar.getInstance();
        mCalender.setTime(new Date(time));

        int hour = mCalender.get(Calendar.HOUR_OF_DAY);
        int minute = mCalender.get(Calendar.MINUTE);

        mCalender.set(Calendar.HOUR_OF_DAY, 0);
        mCalender.set(Calendar.MINUTE, 0);
        mCalender.set(Calendar.SECOND, 0);

        mStartDateLong = mCalender.getTime().getTime();
        updateDateField(mStartDate, mCalender.getTime());

        mStartTimeLong = (hour * 60 + minute) * 1000;
        updateTimeField(mStartTime, hour, minute);

    }

    private void updateEndDateTime(long time) {
        Calendar mCalender = Calendar.getInstance();
        mCalender.setTime(new Date(time));

        int hour = mCalender.get(Calendar.HOUR_OF_DAY);
        int minute = mCalender.get(Calendar.MINUTE);

        mCalender.set(Calendar.HOUR_OF_DAY, 0);
        mCalender.set(Calendar.MINUTE, 0);
        mCalender.set(Calendar.SECOND, 0);

        mEndDateLong = mCalender.getTime().getTime();
        updateDateField(mEndDate, mCalender.getTime());

        mEndTimeLong = (hour * 60 + minute) * 1000;
        updateTimeField(mEndTime, hour, minute);
    }


    private void updateDateField(EditText textField, Date date) {
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        textField.setText(selectedDate);
    }

    private void updateTimeField(EditText textField, int hourOfDay, int minute) {
        int hour = hourOfDay % 12;
        String ampmStr = "AM";
        if (hourOfDay > hour) {
            ampmStr = "PM";
        }
        String minuteStr =String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

        String hourStr = String.valueOf(hour);
        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        String timeStr = hourStr + ":" + minuteStr + " " + ampmStr;
        textField.setText(timeStr);
    }
}