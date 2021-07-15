package com.example.event_cord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.event_cord.RestClient.GetDataService;
import com.example.event_cord.RestClient.RestClient;
import com.example.event_cord.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistration extends AppCompatActivity {

    public static String TAG = "UserRegistration";

    private Button submitButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

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
                GetDataService service = RestClient.getRetrofit().create(GetDataService.class);

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

    private void startLoginActivity() {
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }
}