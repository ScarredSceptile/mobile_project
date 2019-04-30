package com.example.mobile_project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LobbyActivity extends AppCompatActivity{

    ConnectionLifecycleCallback connectionLifecycleCallback;
    EditText room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        final Button btn = findViewById(R.id.createBtn);
        room = findViewById(R.id.roomName);

        if (room.getText().toString().length() == 0) {
            btn.setEnabled(false);
        }

        room.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (room.getText().toString().length() == 0) {
                    btn.setEnabled(false);
                }
                else btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertising();
            }
        });
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(LobbyActivity.this).startAdvertising(room.getText().toString(),
                "@string/service_id", connectionLifecycleCallback,
                advertisingOptions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LobbyActivity.this, "Unable to create lobby", Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        });
    }
}
