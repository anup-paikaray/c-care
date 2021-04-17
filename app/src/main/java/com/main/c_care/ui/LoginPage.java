package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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

public class LoginPage extends AppCompatActivity {
    private TextInputEditText Email, Password;
    public static final String TAG = "LoginPage";
    private Button Login, SignUp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Email = findViewById(R.id.editText_email);
        Password = findViewById(R.id.editText_password);
        Login = findViewById(R.id.btn_login);
        SignUp = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progress_loading);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Email.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String email, String password) {
        final String url = "https://ccareapp.000webhostapp.com/";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("passw", password)
                .build();
        Request request = new Request.Builder()
                .url(url + "personal_login.php")
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
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            Toast.makeText(LoginPage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getInt("success") == 1 ) {
                                JSONObject user = jsonObject.getJSONObject("personal");
                                Intent intent = new Intent(LoginPage.this, MainActivity.class)
                                    .putExtra("PID", user.getString("pid"))
                                    .putExtra("NAME", user.getString("name"))
                                    .putExtra("EMAIL", user.getString("email"))
                                    .putExtra("STATUS", user.getString("status"));
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