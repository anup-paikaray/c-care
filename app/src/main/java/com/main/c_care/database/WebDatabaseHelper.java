package com.main.c_care.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.Nullable;

import com.main.c_care.MainActivity;
import com.main.c_care.ui.LoginPage;
import com.main.c_care.ui.SignupPage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebDatabaseHelper {
    final String url = "http://192.168.29.74/c-care/";
    OkHttpClient client;
    private String name;
    private String email;
    private String password;
    private String phone;

    public WebDatabaseHelper() {
        client = new OkHttpClient();
    }

//    public boolean insertData(String Name, String Email, String Password, String Phone) throws IOException {
//        final String[] myResponse = new String[1];
//        RequestBody formBody = new FormBody.Builder()
//                .add("name", Name)
//                .add("email", Email)
//                .add("passw", Password)
//                .add("phone", Phone)
//                .build();
//        Request request = new Request.Builder()
//                .url(url + "personal_create.php")
//                .post(formBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    myResponse[0] = response.body().string();
//                }
//            }
//        });
//        return Boolean.valueOf(myResponse[0]);
//    }

    public void insertData(String Name, String Email, String Password, String Phone) {
        RequestBody formBody = new FormBody.Builder()
                .add("name", Name)
                .add("email", Email)
                .add("passw", Password)
                .add("phone", Phone)
                .build();
        Request request = new Request.Builder()
                .url(url + "personal_create.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                }
            }
        });
    }

    public String validate(String Email, String Password) {
        final String[] myResponse = new String[1];
        Request request = new Request.Builder()
                .url(url + "personal_login.php")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    myResponse[0] = response.body().string();

                }
            }
        });
        return myResponse[0];
    }
}

