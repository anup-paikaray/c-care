package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.main.c_care.MainActivity;
import com.main.c_care.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupPage extends AppCompatActivity {
    EditText name, email, password, phone;
    Button btnSignUp, btnCancel;
    ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progress_loading);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                insertData();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void insertData() {
        final String url = "https://ccareapp.000webhostapp.com/";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("name", name.getText().toString())
                .add("email", email.getText().toString())
                .add("passw", password.getText().toString())
                .add("phone", phone.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(url + "personal_create.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    SignupPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(myResponse);
                                Toast.makeText(SignupPage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.getInt("success") == 1) {
                                    Intent intent = new Intent(SignupPage.this, MainActivity.class);
                                    intent.putExtra("PID", jsonObject.getString("pid"));
                                    intent.putExtra("NAME", name.getText().toString());
                                    intent.putExtra("EMAIL", name.getText().toString());
                                    intent.putExtra("STATUS", 0);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}