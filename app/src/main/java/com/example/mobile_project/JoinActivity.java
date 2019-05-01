package com.example.mobile_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JoinActivity extends AppCompatActivity {

    ListView list;
    static List<String> roomIds;
    static ArrayAdapter adapter;

    private final SimpleArrayMap<Long, Payload> incomingFilePayload = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Payload> completedFilePayloads = new SimpleArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        list = findViewById(R.id.roomList);
        roomIds = new ArrayList<>();
        startDiscovery();
    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(JoinActivity.this)
                .startDiscovery("@string/service_id", new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(@NonNull final String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                        System.out.println(s);
                        roomIds.add(discoveredEndpointInfo.getEndpointName());
                        adapter = new ArrayAdapter(JoinActivity.this, android.R.layout.simple_list_item_1, roomIds);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(JoinActivity.this, "test", Toast.LENGTH_LONG).show();
                                Nearby.getConnectionsClient(JoinActivity.this)
                                        .requestConnection("test", s, new ConnectionLifecycleCallback() {
                                            @Override
                                            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                                                Nearby.getConnectionsClient(JoinActivity.this).acceptConnection(s, new PayloadCallback() {
                                                    @Override
                                                    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                                        System.out.println("Paylaod received");
                                                        incomingFilePayload.put(payload.getId(), payload);
                                                    }

                                                    private void processFilePayload(long payloadId) {
                                                        // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
                                                        // payload is completely received. The file payload is considered complete only when both have
                                                        // been received.
                                                        Payload filePayload = completedFilePayloads.get(payloadId);
                                                        if (filePayload != null) {
                                                            completedFilePayloads.remove(payloadId);

                                                            // Get the received file (which will be in the Downloads folder)
                                                            File payloadFile = filePayload.asFile().asJavaFile();

                                                            DrawActivity.payloadIsReceived();
                                                            DrawActivity.getImage(payloadFile);
                                                            DrawActivity.startGuess();
                                                        }
                                                    }


                                                    @Override
                                                    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                                                        if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                                                            long payloadId = payloadTransferUpdate.getPayloadId();
                                                            Payload payload = incomingFilePayload.remove(payloadId);
                                                            completedFilePayloads.put(payloadId, payload);
                                                            processFilePayload(payloadId);
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                                                System.out.println("Connected");
                                                final Intent draw = new Intent(JoinActivity.this, DrawActivity.class);

                                                draw.putExtra(DrawActivity.ENDPOINTID, s);

                                                startActivity(draw);

                                            }

                                            @Override
                                            public void onDisconnected(@NonNull String s) {
                                                //We have been disconnected, ono
                                                System.out.println("Disonnected");
                                                finish();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("Success");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getMessage());

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onEndpointLost(@NonNull String s) {

                    }
                }, discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Yay, we can look for advertisers
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinActivity.this, "Unable to search for lobbies.", Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        });
    }

}