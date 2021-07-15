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
import com.example.event_cord.model.Notice;
import com.example.event_cord.model.UserEventPair;
import com.example.event_cord.utility.Helper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNoticeActivity extends AppCompatActivity {

    private Button mCreateButton;
    private EditText mTitle;
    private EditText mDescription;

    private int mEventId;
    private int mCreatorId;
    private String mEventTitle;
    private String mEventDescription;
    private long mLastModifyTime;

    private int mNoticeId;
    private String mNoticeTitle;
    private String mNoticeDescription;
    private boolean mIsEditing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        Bundle bundle = getIntent().getExtras();
        mEventId = bundle.getInt(Constants.EVENT_ID_KEY);
        mCreatorId = bundle.getInt(Constants.EVENT_CREATOR_ID_KEY);
        mEventTitle = bundle.getString(Constants.EVENT_TITLE_KEY);
        mEventDescription = bundle.getString(Constants.EVENT_DESCRIPTION_KEY);
        mLastModifyTime = bundle.getLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY);

        mNoticeId = bundle.getInt(Constants.NOTICE_ID_KEY);
        mNoticeTitle = bundle.getString(Constants.NOTICE_TITLE_KEY);
        mNoticeDescription = bundle.getString(Constants.NOTICE_DESCRIPTION_KEY);
        mIsEditing = bundle.getBoolean(Constants.NOTICE_IS_EDITING_KEY, false);

        mCreateButton = findViewById(R.id.createNotice);
        mTitle = findViewById(R.id.EditTextTitle);
        mDescription = findViewById(R.id.EditTextDescription);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEditing) {
                    createNotice();
                } else {
                    updateNotice();
                }

            }
        });

        if(mIsEditing) {
            mTitle.setText(mNoticeTitle);
            mDescription.setText(mNoticeDescription);
            mCreateButton.setText("Update Notice");
        }
    }

    private void updateNotice() {
        int userId = Helper.getLoggedinUserId(this);
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();

        Notice notice = new Notice(mNoticeId, userId, mEventId, title, description);
        GetDataService service = RestClient.getRetrofit().create(GetDataService.class);
        Call<Void> call = service.updateNotice(notice);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EVENT_ID_KEY, mEventId);
                bundle.putString(Constants.EVENT_TITLE_KEY, mEventTitle);
                bundle.putString(Constants.EVENT_DESCRIPTION_KEY, mEventDescription);
                bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, mLastModifyTime);
                bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, mCreatorId);

                Intent intent = new Intent(CreateNoticeActivity.this, EventDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateNoticeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void createNotice() {
        int userId = Helper.getLoggedinUserId(this);
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();

        Notice notice = new Notice(0, userId, mEventId, title, description);
        GetDataService service = RestClient.getRetrofit().create(GetDataService.class);
        Call<Notice> call = service.createNotice(notice);

        call.enqueue(new Callback<Notice>() {
            @Override
            public void onResponse(Call<Notice> call, Response<Notice> response) {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EVENT_ID_KEY, mEventId);
                bundle.putString(Constants.EVENT_TITLE_KEY, mEventTitle);
                bundle.putString(Constants.EVENT_DESCRIPTION_KEY, mEventDescription);
                bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, mLastModifyTime);
                bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, mCreatorId);

                Intent intent = new Intent(CreateNoticeActivity.this, EventDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Notice> call, Throwable t) {
                Toast.makeText(CreateNoticeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}