package com.main.c_care.user;

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
import com.main.c_care.ui.LoginPage;
import com.main.c_care.ui.SignupPage;

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

public class ModifyUser extends AppCompatActivity {
    EditText name, email, old_pass, new_pass, phone;
    Button btnConfirm, btnCancel;
    ProgressBar progressBar;
    final String url = "https://ccareapp.000webhostapp.com/";
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        MainActivity mainActivity = new MainActivity();
        MainActivity.UserData userData = mainActivity.new UserData();

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        old_pass = findViewById(R.id.editTextOldPass);
        new_pass = findViewById(R.id.editTextNewPass);
        phone = findViewById(R.id.editTextPhone);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_loading);

        name.setText(userData.name);
        email.setText(userData.email);
        phone.setText(userData.phone);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                validate();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void validate() {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email.getText().toString())
                .add("passw", old_pass.getText().toString())
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
                    ModifyUser.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            Toast.makeText(ModifyUser.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getInt("success") == 1) {
                                JSONObject user = jsonObject.getJSONObject("personal");
                                modify(user.getInt("pid"));
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

    private void modify(int pid) {
        RequestBody formBody = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
                .add("name", name.getText().toString())
                .add("email", email.getText().toString())
                .add("passw", new_pass.getText().toString())
                .add("phone", phone.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(url + "personal_modify.php")
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
                    ModifyUser.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            Toast.makeText(ModifyUser.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getInt("success") == 1)
                                finish();
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