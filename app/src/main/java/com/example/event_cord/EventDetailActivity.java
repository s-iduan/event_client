package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.adapter.NoticeAdapter;
import com.example.event_cord.adapter.OnNoticeClickListener;
import com.example.event_cord.adapter.OnNoticeDeletionHandler;
import com.example.event_cord.adapter.OnNoticeEditHandler;
import com.example.event_cord.model.Constants;
import com.example.event_cord.model.Notice;
import com.example.event_cord.model.UserEventPair;
import com.example.event_cord.utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mTextViewEventName;
    private TextView mTextViewEventDescription;
    private TextView mTextViewCreatedBy;
    private TextView mTextViewLastModifyTime;
    private Button mJoinEvent;
    private Button mLeaveEvent;
    private FloatingActionButton mFloatingActionButton;

    private Toolbar toolbar;

    private MenuItem calendarItem;
    private MenuItem listItem;

    private int mEventId;
    private String mEventTitle;
    private String mEventDescription;
    private int mCreatorId;
    private long mLastModifyTime;

    private boolean isUserInEvent;

    private NoticeAdapter adapter;
    private RecyclerView recyclerView;

    private OnNoticeClickListener mOnNoticeClickListener = new OnNoticeClickListener() {
        @Override
        public void onItemClicked(Notice item) {
            Toast.makeText(EventDetailActivity.this, item.getTitle() + " " + item.getDescription(), Toast.LENGTH_LONG).show();
        }
    };

    private OnNoticeDeletionHandler mOnNoticeDeletionHandler = new OnNoticeDeletionHandler() {
        @Override
        public void deleteNotice(int noticeId) {
            GetDataService service = RestClient.getRetrofit(EventDetailActivity.this).create(GetDataService.class);
            Call<Void> call = service.deleteNotice(noticeId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    updateNoticeList();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
    };

    private OnNoticeEditHandler onNoticeEditHandler = new OnNoticeEditHandler() {
        @Override
        public void updateNotice(Notice notice) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.NOTICE_IS_EDITING_KEY, true);
            bundle.putInt(Constants.NOTICE_ID_KEY, notice.getId());
            bundle.putString(Constants.NOTICE_TITLE_KEY, notice.getTitle());
            bundle.putString(Constants.NOTICE_DESCRIPTION_KEY, notice.getDescription());

            bundle.putInt(Constants.EVENT_ID_KEY, mEventId);
            bundle.putString(Constants.EVENT_TITLE_KEY, mEventTitle);
            bundle.putString(Constants.EVENT_DESCRIPTION_KEY, mEventDescription);
            bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, mLastModifyTime);
            bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, mCreatorId);

            Intent intent = new Intent(EventDetailActivity.this, CreateNoticeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        mCreatorId = bundle.getInt(Constants.EVENT_CREATOR_ID_KEY);
        mEventId = bundle.getInt(Constants.EVENT_ID_KEY);
        mEventTitle = bundle.getString(Constants.EVENT_TITLE_KEY);
        mEventDescription = bundle.getString(Constants.EVENT_DESCRIPTION_KEY);
        mLastModifyTime = bundle.getLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY);

        mTextViewEventName = findViewById(R.id.textViewEventName);
        mTextViewEventDescription = findViewById(R.id.textViewEventDescription);
        mTextViewCreatedBy = findViewById(R.id.textViewCreatedBy);
        mTextViewLastModifyTime = findViewById(R.id.textViewLastModifyTime);

        mTextViewEventName.setText(mEventTitle);
        mTextViewEventDescription.setText(mEventDescription);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        String timeStr = sdf.format(mLastModifyTime);
        mTextViewLastModifyTime.setText(timeStr);
        mFloatingActionButton = findViewById(R.id.fabAddButton);

        mJoinEvent = findViewById(R.id.joinEvent);
        mLeaveEvent = findViewById(R.id.leaveEvent);
        mJoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinEvent();
            }
        });

        mLeaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEvent();
            }
        });


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EVENT_ID_KEY, mEventId);
                bundle.putString(Constants.EVENT_TITLE_KEY, mEventTitle);
                bundle.putString(Constants.EVENT_DESCRIPTION_KEY, mEventDescription);
                bundle.putLong(Constants.EVENT_LAST_MODIFIED_TIME_KEY, mLastModifyTime);
                bundle.putInt(Constants.EVENT_CREATOR_ID_KEY, mCreatorId);
                Intent intent = new Intent(EventDetailActivity.this, CreateNoticeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (mCreatorId == Helper.getLoggedinUserId(this)) {
            mLeaveEvent.setEnabled(false);
            mJoinEvent.setEnabled(false);
        }

        checkIfUserInTheEvent();
        updateNoticeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        calendarItem = menu.findItem(R.id.calendarMenuItem);
        listItem = menu.findItem(R.id.listMenuItem);

        calendarItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Helper.navigateToCalendarView(EventDetailActivity.this);
                return true;
            }
        });

        listItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Helper.navigateToListView(EventDetailActivity.this);
                return true;
            }
        });
        return true;
    }

    void checkIfUserInTheEvent() {
        GetDataService service = RestClient.getRetrofit(this).create(GetDataService.class);
        UserEventPair pair = new UserEventPair(Helper.getLoggedinUserId(this), mEventId, 0);
        Call<ResponseBody> call = service.checkUserEvent(pair.getUserId(), pair.getEventId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body().contentLength() == 0) {
                    isUserInEvent = false;
                } else {
                    isUserInEvent = true;
                }
                refresh();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    void joinEvent() {
        GetDataService service = RestClient.getRetrofit(this).create(GetDataService.class);
        UserEventPair pair = new UserEventPair(Helper.getLoggedinUserId(this), mEventId, 0);
        Call<UserEventPair> call = service.joinEvent(pair);

        call.enqueue(new Callback<UserEventPair>() {
            @Override
            public void onResponse(Call<UserEventPair> call, Response<UserEventPair> response) {
                isUserInEvent = true;
                refresh();
            }

            @Override
            public void onFailure(Call<UserEventPair> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    void leaveEvent() {
        GetDataService service = RestClient.getRetrofit(this).create(GetDataService.class);
        UserEventPair pair = new UserEventPair(Helper.getLoggedinUserId(this), mEventId, 0);
        Call<UserEventPair> call = service.leaveEvent(pair);

        call.enqueue(new Callback<UserEventPair>() {
            @Override
            public void onResponse(Call<UserEventPair> call, Response<UserEventPair> response) {
                isUserInEvent = false;
                refresh();
            }

            @Override
            public void onFailure(Call<UserEventPair> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    void updateNoticeList() {
        GetDataService service = RestClient.getRetrofit(this).create(GetDataService.class);
        Call<List<Notice>>call = service.getNotices(mEventId);

        call.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                generateNoticeList(response.body());
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {

            }
        });
    }

    private void generateNoticeList(List<Notice> noticeList) {
        recyclerView = findViewById(R.id.noticeRecyclerView);
        adapter = new NoticeAdapter(noticeList, mOnNoticeClickListener, mOnNoticeDeletionHandler, onNoticeEditHandler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventDetailActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    void refresh() {
        if (isUserInEvent) {
            mJoinEvent.setVisibility(View.GONE);
            mLeaveEvent.setVisibility(View.VISIBLE);
        } else {
            mJoinEvent.setVisibility(View.VISIBLE);
            mLeaveEvent.setVisibility(View.GONE);
        }
    }
}