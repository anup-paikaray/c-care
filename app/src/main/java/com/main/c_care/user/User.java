package com.main.c_care.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.main.c_care.MainActivity;
import com.main.c_care.R;
import com.main.c_care.geofence.MapGeofence;
import com.main.c_care.ui.LoginPage;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class User extends Fragment {
    Button logout, modify, addHome;
    ImageView qrImage;
    MainActivity activity = (MainActivity) getActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout = view.findViewById(R.id.btn_logout);
        modify = view.findViewById(R.id.btn_modify);
        addHome = view.findViewById(R.id.btn_add_home);
        qrImage = view.findViewById(R.id.qrPlaceHolder);

        String data = String.valueOf(activity.userData.pid);
        if(data.isEmpty()){
            Toast.makeText(getContext(), "Invalid User", Toast.LENGTH_SHORT).show();
        } else {
            QRGEncoder qrgEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,500);
            try {
                Bitmap qrBits = qrgEncoder.encodeAsBitmap();
                qrImage.setImageBitmap(qrBits);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ModifyUser.class);
                intent.putExtra("USERNAME", activity.userData.name);
                intent.putExtra("EMAIL", activity.userData.email);
                intent.putExtra("PHONE", activity.userData.phone);
                intent.putExtra("PID", activity.userData.pid);
                startActivity(intent);
            }
        });

        addHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapGeofence map = new MapGeofence();
                Bundle bundle = new Bundle();
                bundle.putInt("ADDHOME", 1);
                map.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.container, map).commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }
}
