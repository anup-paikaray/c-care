package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.main.c_care.MainActivity;
import com.main.c_care.database.LocalDatabaseHelper;
import com.main.c_care.R;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupPage extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText password;
    EditText phone;
    Button btnSignUp;
    Button btnCancel;
    LocalDatabaseHelper myDb;
//    WebDatabaseHelper myWeb;

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
                new FeedTask().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        myDb = new LocalDatabaseHelper(this);
//        myWeb = new WebDatabaseHelper();
    }
//
//    private boolean insertData() {
//        String Name = name.getText().toString();
//        String Email = email.getText().toString();
//        String Password = password.getText().toString();
//        String Phone = phone.getText().toString();
//        return myDb.insertData(Name, Email, Password, Phone);
//        return myWeb.insertData(Name, Email, Password, Phone);
//    }

    public void SignUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class FeedTask extends AsyncTask<String, Void, String> {
//        final String url = "http://192.168.29.74/c-care/";
        final String url = "https://ccareapp.000webhostapp.com/";
        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("name", name.getText().toString())
                        .add("email", email.getText().toString())
                        .add("passw", password.getText().toString())
                        .add("phone", phone.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url(url + "test.php")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().toString();
                toast(result);
                return result;
            } catch (Exception e) {
                toast(e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            toast(s);
//            SignUp();
        }
    }

    private void toast(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }
}