package com.example.mobile_project;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class JoinActivity extends AppCompatActivity {

    EndpointDiscoveryCallback endpointDiscoveryCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        //startDiscovery();

    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(JoinActivity.this)
                .startDiscovery("@string/service_id", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinActivity.this,
                        "Unable to search for lobbies.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}