package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.main.c_care.database.LocalDatabaseHelper;
import com.main.c_care.MainActivity;
import com.main.c_care.R;
import com.main.c_care.database.WebDatabaseHelper;

public class LoginPage extends AppCompatActivity {
    private TextInputEditText Email;
    private TextInputEditText Password;
    public static final String TAG = "LoginPage";
    private Button Login;
    private Button SignUp;
    LocalDatabaseHelper myDb;
    WebDatabaseHelper myWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Email = findViewById(R.id.editText_email);
        Password = findViewById(R.id.editText_password);
        Login = findViewById(R.id.btn_login);
        SignUp = findViewById(R.id.btn_signup);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                validate(Email.getText().toString(), Password.getText().toString());
                login(myWeb.validate(Email.getText().toString(), Password.getText().toString()));
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpPage();
            }
        });
//        myDb = new LocalDatabaseHelper(this);
        myWeb = new WebDatabaseHelper();
    }

    private void SignUpPage() {
        Intent intent = new Intent(this, SignupPage.class);
        startActivity(intent);
    }

    private void validate(String email, String password) {
        Cursor res = myDb.getCredentialData();
        while (res.moveToNext()) {
            String Email = res.getString(2);
            String Password = res.getString(3);
            Log.d(TAG, "validate: " + Email + "  " + Password);
            if (email.equals(Email) && password.equals(Password)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
    }

    private void login(String response) {
        Toast.makeText(this, "Response: " + response, Toast.LENGTH_SHORT).show();

        if (response != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }
}