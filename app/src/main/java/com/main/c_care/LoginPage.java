package com.main.c_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    public static final String TAG = "LoginPage";
    private Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Name = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btnLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String username, String password) {
        Log.d(TAG, (username == "Admin") + ":" + (password == "1234"));
        if (username.equals("Admin") && password.equals("1234")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
