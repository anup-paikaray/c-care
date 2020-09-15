package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.main.c_care.DatabaseHelper;
import com.main.c_care.MainActivity;
import com.main.c_care.R;

public class SignupPage extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText password;
    EditText phone;
    Button btnSignUp;
    Button btnCancel;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPass);
        phone = findViewById(R.id.editTextPhone);
        btnSignUp = findViewById(R.id.btn_signup);
        btnCancel = findViewById(R.id.btn_cancel);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        myDb = new DatabaseHelper(this);
    }

    private boolean insertData() {
        String Name = name.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        String Phone = phone.getText().toString();
        return myDb.insertData(Name, Email, Password, Phone);
    }

    private void SignUp() {
        if (insertData()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(this, "DATA NOT INSERTED", Toast.LENGTH_SHORT).show();
    }
}