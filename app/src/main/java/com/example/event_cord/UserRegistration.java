package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.model.User;
import com.example.event_cord.utility.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistration extends AppCompatActivity {

    public static String TAG = "UserRegistration";

    private Button submitButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Toolbar toolbar;

    private MenuItem calendarItem;
    private MenuItem listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submitButton = findViewById(R.id.register);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                User user = new User(-1, name, email, password, 0);
                GetDataService service = RestClient.getRetrofit(UserRegistration.this).create(GetDataService.class);

                Call<User> call = service.createUser(user);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User createdUser = response.body();
                        startLoginActivity();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        Toast.makeText(UserRegistration.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        calendarItem = menu.findItem(R.id.calendarMenuItem);
        listItem = menu.findItem(R.id.listMenuItem);

        calendarItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Helper.navigateToCalendarView(UserRegistration.this);
                return true;
            }
        });

        listItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Helper.navigateToListView(UserRegistration.this);
                return true;
            }
        });
        return true;
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }
}