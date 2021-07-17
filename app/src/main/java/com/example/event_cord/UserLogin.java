package com.example.event_cord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.model.Constants;
import com.example.event_cord.model.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserLogin extends AppCompatActivity {
    public static String TAG = "UserRegistration";

    private Button submitButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PATH_LOGGED_IN_USER, Context.MODE_PRIVATE);
        String loggedinUser = sharedPreferences.getString(Constants.USER_NAME, "");
        long expiration = sharedPreferences.getLong(Constants.EXPIRATION, -1);

        if (loggedinUser != "" && expiration < LocalDateTime.now().toLocalDate().toEpochDay()) {
            startEventActivity();
        } else {
            startLogin();
        }
    }

    private void startLogin() {
        submitButton = findViewById(R.id.login);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerLink = findViewById(R.id.textViewRegister);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, UserRegistration.class);
                startActivity(intent);
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( emailEditText.getText().toString() != "" &&
                        passwordEditText.getText().toString() != "") {
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( emailEditText.getText().toString() != "" &&
                        passwordEditText.getText().toString() != "") {
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        submitButton.setEnabled(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                User user = new User(-1, "", email, password, 0);
                GetDataService service = RestClient.getRetrofit().create(GetDataService.class);

                Call<User> call = service.getLoginUser(user);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User loggedInUser = response.body();
                        loginUser(loggedInUser);
                        startEventActivity();

                        Toast.makeText(UserLogin.this, "User: " + loggedInUser.getName() + " logged in.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        Toast.makeText(UserLogin.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void loginUser (User loggedInUser) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PATH_LOGGED_IN_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_NAME, loggedInUser.getName());
        editor.putString(Constants.USER_EMAIL, loggedInUser.getEmail());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plus(30, ChronoUnit.DAYS); // add 30 days to expiration
        long expiration = later.toLocalDate().toEpochDay();
        editor.putLong(Constants.EXPIRATION, expiration);
        editor.putInt(Constants.USER_ID, loggedInUser.getId());
        editor.commit();
    }

    private void startEventActivity() {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
}