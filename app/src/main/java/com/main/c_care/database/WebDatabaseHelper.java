package com.main.c_care.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

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

    public WebDatabaseHelper() {
//        new FeedTask().execute();
        client = new OkHttpClient();
    }

//    public class FeedTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                OkHttpClient client = new OkHttpClient();
//                RequestBody formBody = new FormBody.Builder()
//                        .add("name", Name)
//                        .add("email", Email)
//                        .add("passw", Password)
//                        .add("phone", Phone)
//                        .build();
//                Request request = new Request.Builder()
//                        .url(url + "personal_create.php")
//                        .post(formBody)
//                        .build();
//                Response response = client.newCall(request).execute();
//                String result = response.body().toString();
//                return result;
//            } catch (Exception e) {
//                return null;
//            }
//        }
//    }

    public boolean insertData(String Name, String Email, String Password, String Phone) throws IOException {
        final String[] myResponse = new String[1];
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
//        Response response = client.newCall(request).execute();
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
        return Boolean.valueOf(myResponse[0]);
//        return Boolean.valueOf(response.body().string());
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



